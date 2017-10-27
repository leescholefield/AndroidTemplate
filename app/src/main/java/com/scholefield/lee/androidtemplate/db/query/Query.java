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

    /**
     * Returns the SQL table this query should be performed on.
     */
    String getTable();

    /**
     * Returns the SQL where clause or {@code null} if one was not given.
     */
    @Nullable
    String getWhereClause();

    /**
     * Returns an array of table columns that this query should be performed on. If this is not applicable this will
     * return an empty array.
     */
    String[] getColumns();

}
