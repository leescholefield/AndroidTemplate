package com.scholefield.lee.androidtemplate.db.query;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches multiple tables using an INNER JOIN. Unlike other Queries this uses a Builder pattern for instantiation.
 */
public class MultitableSearchQuery implements Query {

    private String firstTable;
    private List<Table> joinedTables;
    private String whereCondition;
    private String[] columns;

    /**
     * Private constructor to avoid instantiation.
     */
    private MultitableSearchQuery(){}

    public static class Builder {

        private String table;
        private String[] columns;
        private List<Table> tables;
        private String where;

        /**
         * @param table initial table to search.
         */
        public Builder(String table) {
            this.table = table;
            tables = new ArrayList<>();
        }

        /**
         * Appends a new table for an inner join.
         *
         * @param table table name.
         * @param onClause SQL on clause (not including "ON"). For example, "artists.id = album.artist_id".
         */
        public Builder table(String table, String onClause) {
            Table t = new Table(table, onClause);
            tables.add(t);
            return this;
        }

        /**
         * Columns to return. Should be prefaced with the table name. For example, "users.name"
         */
        public Builder columns(String[] columns) {
            this.columns = columns;
            return this;
        }

        /**
         * SQL where clause.
         */
        public Builder where(String where) {
            this.where = where;
            return this;
        }

        /**
         * Creates a new {@link MultitableSearchQuery}.
         *
         * @throws IllegalArgumentException if no tables have to been created to join to.
         */
        public MultitableSearchQuery build() {
            if (tables.size() == 0) {
                throw new IllegalArgumentException("no joinedTables specified");
            }
            return new MultitableSearchQuery(table, tables, where, columns);
        }
    }

    /**
     * Private constructor used by {@link Builder} class.
     */
    private MultitableSearchQuery(String firstTable, List<Table> joinedTables, String where, String[] columns) {
        this.firstTable = firstTable;
        this.joinedTables = joinedTables;
        this.whereCondition = where;
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
        query += tablesToString();

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
     * package-private so it can be tested.
     */
    String tablesToString() {
        String query = "FROM " + firstTable;
        for (Table table : joinedTables) {
            query += " INNER JOIN " + table.name + " ON " + table.onClause;
        }

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
     * Utility class for wrapping a joined table and on clause.
     */
    private static class Table {

        private String name;
        private String onClause;

        private Table(String table, String onClause) {
            this.name = table;
            this.onClause = onClause;
        }
    }
}
