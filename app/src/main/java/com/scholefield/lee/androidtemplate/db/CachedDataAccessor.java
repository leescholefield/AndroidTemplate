package com.scholefield.lee.androidtemplate.db;

import com.scholefield.lee.androidtemplate.cache.Cache;
import com.scholefield.lee.androidtemplate.cache.SoftCache;
import com.scholefield.lee.androidtemplate.db.query.DeleteQuery;
import com.scholefield.lee.androidtemplate.db.query.SearchQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of {@link DataAccessor} with a built-in {@link Cache}. The main use-case of this class is for
 * when you are dealing will POJOs. The cache will hold a list of {@code T}s for each get or put request made. These will
 * be stored by the table or query used in the request.
 *
 * Example:
 * <pre>
 *     {@code
 *     cachedDataAccessor.put(foo, "foo_table");
 *     }
 * </pre>
 * This will first call database#insert. It it is successfully inserted it will then store it in the cache in a list with
 * "foo_table" as the key.
 *
 * @param <T> the type of object saved to the database.
 */
public class CachedDataAccessor<T> extends SimpleDataAccessor<T> {

    private Cache<String, List<T>> cache;
    private static final int DEFAULT_CACHE_SIZE = 5;

    private DataReader<T> reader;
    private DataWriter<T> writer;

    public CachedDataAccessor(Database database) {
        super(database);
        cache = new SoftCache<>(DEFAULT_CACHE_SIZE);
    }

    public CachedDataAccessor(Database database, int cacheSize) {
        super(database);
        cache = new SoftCache<>(cacheSize);
    }

    /**
     * Inserts {@code obj} into the given {@code table}. This uses the default writer set via {@link #setDefaultWriter}.
     * If no writer is set this throw a NullPointerException.
     *
     * @param obj object to save.
     * @param table table to save to.
     */
    @Override
    public void put(T obj, String table) {
        if (writer == null) {
            throw new NullPointerException("default writer is null. You must call setWriter");
        }

        put(obj, table, writer);
    }

    /**
     * Inserts {@code obj} into the given {@code table}.
     *
     * @param obj object to save.
     * @param table table to save to.
     * @param writer converts the obj to ContentValues.
     */
    @Override
    public void put(T obj, String table, DataWriter<T> writer) {
        super.put(obj, table, writer);
        // super throws an exception if obj could not be inserted in db so if we get to this we can assume it was inserted
        appendToCacheList(table, obj);
    }

    @Override
    public List<T> get(SearchQuery query, boolean forceUpdate) {
        if (reader == null) {
            throw new NullPointerException("Default reader is null. You must call setReader");
        }

        return get(query, forceUpdate, reader);
    }

    @Override
    public List<T> get(SearchQuery query, boolean forceUpdate, DataReader<T> reader) {
        List<T> results;
        if (!forceUpdate) {
            results = checkCache(query.getQuery());
            if (results != null) {
                return results;
            }
        }
        // super throws an exception if db could not be accessed
        results = super.get(query, forceUpdate, reader);

        cache.put(query.getQuery(), results);

        return results;
    }

    @Override
    public void remove(DeleteQuery query) {
        super.remove(query);
    }

    private void appendToCacheList(String key, T item) {
        List<T> existing = cache.get(key);
        if (existing == null) {
            existing = new ArrayList<>();
            cache.put(key, existing);
        }

        existing.add(item);
    }

    private List<T> checkCache(String key) {
        return cache.get(key);
    }

    @Override
    public void setDefaultReader(DataReader<T> reader) {
        this.reader = reader;
    }

    @Override
    public void setDefaultWriter(DataWriter<T> writer) {
        this.writer = writer;
    }

    Cache<String, List<T>> getCache() {
        return cache;
    }

    void setCache(Cache<String, List<T>> cache) {
        this.cache = cache;
    }
}