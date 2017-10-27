package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;
import android.database.Cursor;
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
public class CachedDataAccessor<T> implements DataAccessor<T> {

    private Database database;

    private Cache<String, List<T>> cache;
    private static final int DEFAULT_CACHE_SIZE = 5;

    private DataReader<T> reader;
    private DataWriter<T> writer;

    public CachedDataAccessor(Database database) {
        cache = new SoftCache<>(DEFAULT_CACHE_SIZE);
        this.database = database;
    }

    public CachedDataAccessor(Database database, int cacheSize) {
        cache = new SoftCache<>(cacheSize);
        this.database = database;
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
        ContentValues cv = writer.toContentValues(obj);
        try {

            long id = database.insert(table, cv);
            if (id != -1) {
                appendToCacheList(table, obj);
            }

        } catch (DatabaseException e) {
            throw new RuntimeException("Could not insert data into the database", e);
        }
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

        Cursor c = null;
        try {
            c = database.get(query);
            results = cursorToItemList(c, reader);
        }
        catch (DatabaseException e) {
            throw new RuntimeException("Could not insert data into the database", e);
        }
        finally {
            if (c != null) c.close();
        }

        cache.put(query.getQuery(), results);

        return results;
    }

    @Override
    public void remove(DeleteQuery query) {
        try {
            database.delete(query);
        } catch (DatabaseException e) {
            throw new RuntimeException("Could not delete data from the database");
        }
    }

    private void appendToCacheList(String key, T item) {
        List<T> existing = cache.get(key);
        if (existing == null) {
            existing = new ArrayList<>();
            cache.put(key, existing);
        }

        existing.add(item);
    }

    private List<T> cursorToItemList(Cursor cursor, DataReader<T> reader) {
        List<T> result = new ArrayList<>();

        while (cursor.moveToNext()) {
            T item = reader.fromCursor(cursor);
            if (item != null) {
                result.add(item);
            }
        }

        return result;
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