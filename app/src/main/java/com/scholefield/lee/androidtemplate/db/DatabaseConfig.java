package com.scholefield.lee.androidtemplate.db;

/**
 * Config class for constructing a new SQLite database.
 */
public interface DatabaseConfig {

    /**
     * Current database version.
     *
     * This MUST be greater than 0.
     */
    int getVersion();

    /**
     * File the database should be saved under.
     */
    String getFileName();

    /**
     * Returns a String array containing create table statements.
     *
     * Example statement:
     *      "CREATE TABLE users(id INTEGER PRIMARY KEY, name TEXT)"
     */
    String[] getTableCreationStatements();

    /**
     * Returns a String array containing delete table statements.
     */
    String[] getTableDeletionStatements();

}
