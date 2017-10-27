package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.scholefield.lee.androidtemplate.db.query.DeleteQuery;
import com.scholefield.lee.androidtemplate.db.query.SearchQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A very simple implementation of {@link DataAccessor}.
 *
 * @param <T> type of object saved to the database.
 */
public class SimpleDataAccessor<T> implements DataAccessor<T> {

    private Database database;

    private DataReader<T> defaultReader;
    private DataWriter<T> defaultWriter;

    public SimpleDataAccessor(Database database) {
        this.database = database;
    }

    /**
     * Saves the {@code obj} to the database using the default {@code DataWriter} set by {@link #setDefaultWriter}.
     * If no writer has been set this will throw a RuntimeException.
     *
     * @param obj   object to save.
     * @param table table to save to.
     */
    @Override
    public void put(T obj, String table) {
        if (defaultWriter == null) {
            throw new NullPointerException("defaultWriter has not been set");
        }

        put(obj, table, defaultWriter);
    }

    /**
     * Saves the {@code obj} to the database using the {@code writer}.
     *
     * @param obj    object to save.
     * @param table  table to save to.
     * @param writer converts the obj to ContentValues.
     */
    @Override
    public void put(T obj, String table, DataWriter<T> writer) {
        ContentValues cv = writer.toContentValues(obj);
        try {
            database.insert(table, cv);
        } catch (DatabaseException e) {
            throw new RuntimeException("Could not insert data into the database", e);
        }
    }

    /**
     * Returns a list of {@link T}s from the database using the {@code query}.
     *
     * @param query       database query.
     * @param forceUpdate if {@code false} this will first check the cache (if the implementation has one) before the database.
     * @param reader      converts the raw data returned from the database to an instance of {@link T}.
     */
    @Override
    public List<T> get(SearchQuery query, boolean forceUpdate, DataReader<T> reader) {
        List<T> results;

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

        return results;
    }

    /**
     * Returns a list of {@link T}s from the database using the {@code query}. This will use the {@link DataReader} set via
     * {@link #setDefaultReader}.
     *
     * @param query       database query.
     * @param forceUpdate if {@code false} this will first check the cache (if the implementation has one) before the database.
     */
    @Override
    public List<T> get(SearchQuery query, boolean forceUpdate) {
        if (defaultReader == null) {
            throw new NullPointerException("Default reader is null. You must call setReader");
        }

        return get(query, forceUpdate, defaultReader);
    }

    /**
     * Performs an SQL delete query.
     */
    @Override
    public void remove(DeleteQuery query) {

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

    /**
     * Sets the default {@link DataWriter} used to convert {@link T} to ContentValues.
     */
    @Override
    public void setDefaultWriter(DataWriter<T> writer) {
        this.defaultWriter = writer;
    }

    /**
     * Sets the default {@link DataReader} used to convert the raw data from the database to an instance of {@link T}.
     */
    @Override
    public void setDefaultReader(DataReader<T> reader) {
        this.defaultReader = reader;
    }
}
