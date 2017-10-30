package com.scholefield.lee.androidtemplate.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An SQL search query for a single table.
 *
 * A SearchQuery is constructed from the following 3 fields:
 *      <STRONG>table</STRONG>: the name of the table if the database.
 *      <STRONG>columns</STRONG>: an array containing the names of the columns to return.
 *      <STRONG>where</STRONG>: an SQL where clause for returning only matching rows.
 * Only the table is mandatory.
 *
 * The following are example of how to instantiate a SearchQuery and the SQL query that will be generated from the given values.
 *
 * All rows from a given table:
 *      <code>new SearchQuery("customers");</code>
 *      SQL string: "SELECT * FROM customers".
 *
 * Specified columns from all rows in a table:
 *      <code>new SearchQuery("customers", new String[]{"name", "address"}, null);</code>
 *      SQL string: "SELECT name, address FROM customers".
 *
 * All rows from a table that match a where clause:
 *      <code>new SearchQuery("customers", "name='john'");</code>
 *      SQL string: "SELECT * FROM customers WHERE name='john'".
 *
 * All rows from a table that match two where clauses:
 *      <code>new SearchQuery("customers", "name='john' AND age > 25");</code>
 *      SQL string: "SELECT * FROM customers WHERE name='john' AND age > 25".
 *
 * Specified columns from rows that match a where clause:
 *       <code>new SearchQuery("customers", new String[]{"address", "age"}, "name='john' AND age > 25);</code>
 *       SQL string: "SELECT address, age FROM customers WHERE name='john' AND age > 25".
 */
public class SearchQuery implements Query {

    private final String table;
    private String where;
    private String[] columnsToReturn;

    /**
     * Constructor with just a table. This will get all rows and columns from the {@code table}.
     */
    public SearchQuery(@NonNull String table) {
        this.table = table;
    }

    /**
     * Constructor with a table and SQL where. This will get all rows and columns if the where matches. Do not include "WHERE"
     * in the where string.
     */
    public SearchQuery(@NonNull String table, @Nullable String where) {
        this(table);
        this.where = where;
    }

    /**
     * Constructor with table, SQL where and columns. This will get the specified columns from a row in the table if the where
     * matches. Do not include "WHERE" in the where string.
     */
    public SearchQuery(@NonNull String table, @Nullable String[] columns, @Nullable String where) {
        this(table, where);
        this.columnsToReturn = columns;
    }

    @Override
    public String getQuery() {
        return buildQuery();
    }

    private String buildQuery() {
        String query = "SELECT ";

        if (columnsToReturn != null && columnsToReturn.length > 0) {
            query += columnsToString();
        } else {
            query += "* ";
        }

        // append table
        query += "FROM " + table;

        // append where
        if (where != null) {
            query += " WHERE " + where;
        }

        return query;
    }

    /**
     * Converts {@link #columnsToReturn} to a comma separated String.
     */
    private String columnsToString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < columnsToReturn.length; i++) {
            builder.append(columnsToReturn[i]);

            if (i != columnsToReturn.length -1) {
                builder.append(", ");
            } else {
                builder.append(" ");
            }
        }

        return builder.toString();
    }
}
