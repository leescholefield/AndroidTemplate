package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.scholefield.lee.androidtemplate.db.query.Query;

/**
 * {@link Database} implementation for interacting with the android SQLite database via the {@code SQLiteOpenHelper} class.
 */
public class SqlDatabase extends SQLiteOpenHelper implements Database {

    private final DatabaseConfig config;

    public SqlDatabase(Context context, DatabaseConfig config) {
        super(context, config.getFileName(), null, config.getVersion());
        this.config = config;
    }

    /**
     * Inserts the {@code data} into the given {@code table}.
     *
     * @param table table to insert into.
     * @param data data to insert.
     *
     * @return the id of the deleted row.
     * @throws DatabaseException if an i/o error occurs.
     */
    @Override
    public long insert(String table, ContentValues data) throws DatabaseException {
        try (SQLiteDatabase db = this.getWritableDatabase()){
            return db.insertOrThrow(table, null, data);
        } catch (SQLiteException e) {
            throw new DatabaseException("Could not insert into table " + table, e);
        }
    }

    /**
     * Performs a Search query on the database and returns a Cursor containing the results. If no results were found the cursor
     * will be empty.
     *
     * Note, the caller is responsible for closing the returned Cursor.
     *
     * @throws DatabaseException if an i/o error occurs.
     */
    @Override
    public Cursor get(Query query) throws DatabaseException {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery(query.getQuery(), null);
        } catch (SQLiteException e) {
            throw new DatabaseException("Could not query the database", e);
        }
    }

    /**
     * Performs a delete query on the database.
     *
     * @param query delete query.
     * @throws DatabaseException if an i/o error occurs.
     */
    @Override
    public void delete(Query query) throws DatabaseException {
        try(SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(query.getQuery());
        } catch (SQLiteException e) {
            throw new DatabaseException("Could not delete from the database", e);
        }
    }

    @Override
    public void update(Query query) throws DatabaseException {
        try(SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(query.getQuery());
        } catch (SQLiteException e) {
            throw new DatabaseException("Could not update the database", e);
        }
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] createStatements = config.getTableCreationStatements();

        if (createStatements == null || createStatements.length == 0) {
            throw new IllegalArgumentException("createStatements == null || createStatements.length == 0");
        }

        for (String s : createStatements) {
            db.execSQL(s);
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String s: config.getTableDeletionStatements()) {
            db.execSQL(s);
        }
        onCreate(db);
    }
}
