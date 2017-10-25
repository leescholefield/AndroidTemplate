package com.scholefield.lee.androidtemplate.db;

import android.database.Cursor;

/**
 * Creates an instance of {@code T} from {@link Cursor}.
 */
public interface DataReader<T> {

    /**
     * Creates an instance of type {@code T} from {@code data}.
     *
     * The {@link DataAccessor} is responsible for closing the Cursor, and moving to the next record.
     */
    T fromCursor(Cursor data);
}
