package com.scholefield.lee.androidtemplate.db;

import com.scholefield.lee.androidtemplate.db.query.SearchQuery;

import java.util.List;

/**
 *
 *
 * @param <T> type of object that is saved to the database
 */
public interface DataAccessor<T> {

    /**
     * Saves the {@code obj} to the database using the default {@code DataWriter} set by {@link #setDefaultWriter}.
     * If no writer has been set this will throw a RuntimeException.
     *
     * @param obj object to save.
     * @param table table to save to.
     */
    void put(T obj, String table);

    /**
     * Saves the {@code obj} to the database using the {@code writer}.
     *
     * @param obj object to save.
     * @param table table to save to.
     * @param writer converts the obj to ContentValues.
     */
    void put(T obj, String table, DataWriter<T> writer);

    List<T> get(SearchQuery query, boolean forceUpdate, DataReader<T> reader);

    List<T> get(SearchQuery query, boolean forceUpdate);

    void setDefaultWriter(DataWriter<T> writer);

    void setDefaultReader(DataReader<T> reader);
}
