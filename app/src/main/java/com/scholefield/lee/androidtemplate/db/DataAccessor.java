package com.scholefield.lee.androidtemplate.db;

import com.scholefield.lee.androidtemplate.db.query.DeleteQuery;
import com.scholefield.lee.androidtemplate.db.query.SearchQuery;

import java.util.List;

/**
 * Acts as an interface between the application code and the database.
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

    /**
     * Returns a list of {@link T}s from the database using the {@code query}.
     *
     * @param query database query.
     * @param forceUpdate if {@code false} this will first check the cache (if the implementation has one) before the database.
     * @param reader converts the raw data returned from the database to an instance of {@link T}.
     */
    List<T> get(SearchQuery query, boolean forceUpdate, DataReader<T> reader);

    /**
     * Returns a list of {@link T}s from the database using the {@code query}. This will use the {@link DataReader} set via
     * {@link #setDefaultReader}.
     *
     * @param query database query.
     * @param forceUpdate if {@code false} this will first check the cache (if the implementation has one) before the database.
     */
    List<T> get(SearchQuery query, boolean forceUpdate);

    /**
     * Performs an SQL delete query.
     */
    void remove(DeleteQuery query);

    /**
     * Sets the default {@link DataWriter} used to convert {@link T} to ContentValues.
     */
    void setDefaultWriter(DataWriter<T> writer);

    /**
     * Sets the default {@link DataReader} used to convert the raw data from the database to an instance of {@link T}.
     */
    void setDefaultReader(DataReader<T> reader);
}
