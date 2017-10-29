package com.scholefield.lee.androidtemplate.db.query;

import android.content.ContentValues;
import android.support.annotation.Nullable;

/**
 * Updates an existing record in a table.
 *
 */
public class UpdateQuery implements Query {

    private String table;
    private ContentValues newValues;
    private String where;

    /**
     *
     * @param table name of table to update
     * @param newValues key is name of column to update and value is new column value.
     * @param where optional where clause.
     */
    public UpdateQuery(String table, ContentValues newValues, String where) {
        this.table = table;
        this.newValues = newValues;
        this.where = where;
    }

    /**
     * Returns a SQLite query String.
     */
    @Override
    public String getQuery() {
        String query = "UPDATE " + table + " SET " + newValuesToString();

        if (where != null) {
            query += " WHERE " + where;
        }

        return query;
    }

    /**
     * Converts {@link #newValues} to a comma-separated list with Strings surrounded by single quotes.
     *
     * Package-private for unit testing.
     */
    String newValuesToString() {
        String query = "";
        String[] keySet = newValues.keySet().toArray(new String[newValues.size()]);
        for (int i = 0; i < keySet.length; i++) {
            Object value = newValues.get(keySet[i]);
            String s = keySet[i] + " = ";
            if (value instanceof String) {
                s += "'" + (String)value + "'";
            } else {
                s += value;
            }

            if (i < keySet.length - 1) {
                s += ", ";
            }

            query += s;
        }

        return query;
    }

    /**
     * Returns the SQL table this query should be performed on.
     */
    @Override
    public String getTable() {
        return null;
    }

    /**
     * Returns the SQL where clause or {@code null} if one was not given.
     */
    @Nullable
    @Override
    public String getWhereClause() {
        return null;
    }

    /**
     * Returns an array of table columns that this query should be performed on. If this is not applicable this will
     * return an empty array.
     */
    @Override
    public String[] getColumns() {
        return new String[0];
    }
}
