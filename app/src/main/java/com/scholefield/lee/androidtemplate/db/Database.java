package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.scholefield.lee.androidtemplate.db.query.Query;

/**
 * Represents the android implementation of an SQLite database.
 */
public interface Database {

    /**
     * Inserts the values contained in {@code data} into the given {@code table}.
     *
     * @param table table to insert into.
     * @param data data to insert.
     * @return the id of the newly inserted row.
     * @throws DatabaseException if the data could not be inserted, or an i/o error occurs.
     */
    long insert(String table, ContentValues data) throws DatabaseException;

    /**
     * Performs a search query and returns the results as a {@link Cursor}. If there are no results the Cursor will be empty.
     *
     * Note, the caller is responsible for closing the cursor.
     *
     * @throws DatabaseException if an i/o error occurs.
     */
    Cursor get(Query query) throws DatabaseException;

    /**
     * Deletes an entry from the database.
     *
     * @throws DatabaseException if an i/o error occurs.
     */
    void delete(Query query) throws DatabaseException;

}
