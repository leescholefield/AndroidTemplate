package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;

/**
 * Creates {@link ContentValues} from an instance of {@code T}.
 */
public interface DataWriter<T> {

    /**
     * Creates {@link ContentValues} from an instance of {@code T}.
     */
    ContentValues toContentValues(T obj);
}
