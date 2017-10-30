package com.scholefield.lee.androidtemplate.db.query;

import android.support.annotation.Nullable;

/**
 * Base class for a SQLite query.
 */
public interface Query {

    /**
     * Returns a SQLite query String.
     */
    String getQuery();
}
