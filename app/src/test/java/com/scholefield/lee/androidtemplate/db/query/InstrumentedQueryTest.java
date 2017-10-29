package com.scholefield.lee.androidtemplate.db.query;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.scholefield.lee.androidtemplate.db.DatabaseConfig;
import com.scholefield.lee.androidtemplate.db.SqlDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

/**
 * Tests each {@link Query} returns the expected results from a database
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class InstrumentedQueryTest {

    private SqlDatabase db;

    @Before
    public void setUp() throws Exception {
        db = new SqlDatabase(RuntimeEnvironment.application, new TestDbConfig());
    }

    @After
    public void cleanUp() throws Exception {
        SQLiteDatabase database = db.getWritableDatabase();
        database.delete("first", null, null);
        database.delete("second", null, null);
        database.delete("third", null, null);
    }

    @Test
    public void searchQuery_with_only_table_set() throws Exception {
        insertTestDataIntoFirstTable();

        SearchQuery query = new SearchQuery("first");

        Cursor c = db.get(query);

        assertEquals(1, c.getCount());
        c.close();
    }

    @Test
    public void searchQuery_with_where_clause() throws Exception {
        insertTestDataIntoFirstTable();
        SearchQuery query = new SearchQuery("first", "name = 'john'");

        Cursor c = db.get(query);

        assertEquals(0, c.getCount());
        c.close();
    }

    @Test
    public void searchQuery_with_columns() throws Exception {
        insertTestDataIntoFirstTable();
        SearchQuery query = new SearchQuery("first", new String[]{"age"}, null);

        Cursor c = db.get(query);

        assertEquals(1, c.getCount());
        assertEquals(1, c.getColumnCount());
        c.close();
    }

    @Test
    public void multitableSearchQuery_with_two_tables() throws Exception {
        insertTestDataIntoFirstTable();
        insertTestDataIntoSecondTable();

        MultitableSearchQuery query = new MultitableSearchQuery.Builder("first")
                .table("second", "first.age = second.age")
                .build();

        Cursor c = db.get(query);
        assertEquals(1, c.getCount());

        c.close();
    }

    @Test
    public void deleteQuery_whole_table() throws Exception {
        insertTestDataIntoFirstTable();

        DeleteQuery query = new DeleteQuery("first");

        db.delete(query);

        Cursor c = db.get(new SearchQuery("first"));
        assertEquals(0, c.getCount());
        c.close();
    }

    @Test
    public void deleteQuery_single_entry() throws Exception {
        insertTestDataIntoFirstTable();
        db.insert("first", createContentValues("jake", 20));

        DeleteQuery query = new DeleteQuery("first", "name='lee'");
        db.delete(query);

        Cursor c = db.get(new SearchQuery("first"));
        assertEquals(1, c.getCount());

        c.close();
    }

    private void insertTestDataIntoFirstTable() throws Exception {
        db.insert("first", createContentValues("lee", 24));
    }

    private void insertTestDataIntoSecondTable() throws Exception {
        db.insert("second", createContentValues("jess", 24));
    }

    private void insertTestDataIntoThirdTable() throws Exception {
        db.insert("third", createContentValues("jack", 18));
    }

    private ContentValues createContentValues(String name, int age) {
        ContentValues cv = new ContentValues(2);
        cv.put("name", name);
        cv.put("age", age);
        return cv;
    }

    private class TestDbConfig implements DatabaseConfig {

        @Override
        public int getVersion() {
            return 1;
        }

        /**
         * File the database should be saved under.
         */
        @Override
        public String getFileName() {
            return "instrumentedquerytest.db";
        }

        @Override
        public String[] getTableCreationStatements() {
            String first = "CREATE TABLE first(id INTEGER PRIMARY KEY, name TEXT, age INTEGER)";
            String second = "CREATE TABLE second(id INTEGER PRIMARY KEY, name TEXT, age INTEGER)";
            String third = "CREATE TABLE third(id INTEGER PRIMARY KEY, name TEXT, age INTEGER)";
            return new String[]{first, second, third};
        }

        /**
         * Returns a String array containing delete table statements.
         */
        @Override
        public String[] getTableDeletionStatements() {
            return new String[0];
        }
    }
}
