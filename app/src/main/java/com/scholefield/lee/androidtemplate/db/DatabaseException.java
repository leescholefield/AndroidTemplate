package com.scholefield.lee.androidtemplate.db;

/**
 * This class is mostly used as a wrapper around {@code SQLiteException}
 */
public class DatabaseException extends Exception {

    DatabaseException(String message) {
        super(message);
    }

    DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
