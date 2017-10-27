package com.scholefield.lee.androidtemplate.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * todo more than two tables.
 * todo doesnt make sense to have getTable, getWhere from query since they return single string
 */
public class MultitableSearchQuery implements Query {

    private String[] tables;
    private String onClause;
    private String whereCondition;
    private String[] columns;

    public MultitableSearchQuery(@NonNull String[] tables, @NonNull String onClause) {
        if (tables.length != 2) {
            throw new IllegalArgumentException("Must have only two tables");
        }

        this.tables = tables;
        this.onClause = onClause;
    }

    public MultitableSearchQuery(@NonNull String[] tables, @NonNull String onClause, String whereClause) {
        this(tables, onClause);
        this.whereCondition = whereClause;
    }

    public MultitableSearchQuery(@NonNull String[] tables, @NonNull String onClause, String whereClause, String[] columns) {
        this(tables, onClause, whereClause);
        this.columns = columns;
    }

    @Override
    public String getQuery() {
        String query = "SELECT ";

        if (columns != null && columns.length != 0) {
            query += columnsToString();
        } else {
            query += "* ";
        }

        // append table
        query += "FROM " + tables[0] + " JOIN " + tables[1] + " ON " + onClause;

        if(whereCondition != null) {
            query = appendWhereConditional(query);
        }

        return query;
    }

    private String appendWhereConditional(String query) {
        query = query + " WHERE " + whereCondition;
        return query;
    }

    /**
     * Converts {@link #columns} to a comma separated String.
     */
    private String columnsToString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i]);

            if (i != columns.length -1) {
                builder.append(", ");
            } else {
                builder.append(" ");
            }
        }

        return builder.toString();
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
