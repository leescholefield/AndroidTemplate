package com.scholefield.lee.androidtemplate.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Deletes the rows matching the {@link #where} clause from {@link #table}. If no {@code where} is given this will delete all
 * rows from the {@code table}.
 */
public class DeleteQuery implements Query {

    private final String table;
    private String where;

    /**
     * If no {@link #where} clause is given this will delete all rows from the given {@code table}.
     *
     * @param table name of the table to delete.
     */
    public DeleteQuery(@NonNull String table) {
        this.table = table;
    }

    /**
     * Deletes the rows matching {@code where} from {@code table}.
     *
     * @param table name of the table to delete from.
     * @param where which rows to delete. Do NOT include "WHERE" in the string itself.
     */
    public DeleteQuery(@NonNull String table, @Nullable String where) {
        this(table);
        this.where = where;
    }


    /**
     * Returns a SQL String in the format "DELETE FROM {@link #table} [WHERE {@link #where}]"
     */
    @Override
    public String getQuery() {
        String query = "DELETE FROM " + table;
        if (where != null) {
            query = query + " WHERE " + where;
        }
        return query;
    }
}
