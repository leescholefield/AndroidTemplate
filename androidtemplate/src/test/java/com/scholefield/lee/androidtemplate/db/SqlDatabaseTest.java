package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.scholefield.lee.androidtemplate.db.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 *
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class SqlDatabaseTest {

    private SqlDatabase classUnderTest;
    private TestDbConfig config = new TestDbConfig();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        classUnderTest = new SqlDatabase(RuntimeEnvironment.application, config);
    }

    @After
    public void cleanUp() throws Exception {
        classUnderTest.getWritableDatabase().execSQL("DROP TABLE IF EXISTS first");
        classUnderTest.getWritableDatabase().execSQL("DROP TABLE IF EXISTS second");
    }


    @Test
    public void table_successfully_created() throws Exception {
        Cursor c = getCursor("SELECT name FROM sqlite_master WHERE type='table' AND name='first'");

        assertEquals(1, c.getCount());

        c.close();
    }

    @Test
    public void multiple_tables_successfully_created() throws Exception {
        Cursor c = getCursor("SELECT * FROM sqlite_master WHERE type='table'");

        // android automatically creates a table for metadata so we expect 3 tables rather than just the 2 in TestConfig
        assertEquals(3, c.getCount());

        c.close();
    }

    @Test
    public void data_successfully_inserted() throws Exception {
        insertTestDataIntoTable1();

        Cursor c = getCursor("SELECT * FROM first");

        assertEquals(2, c.getCount());
        c.moveToFirst();
        assertEquals("lee", c.getString(c.getColumnIndex("name")));
        c.close();
    }

    @Test
    public void calling_insert_with_invalid_table_throws_exception() throws Exception{
        expectedException.expect(Exception.class);

        classUnderTest.insert("non-existent table", new ContentValues());
    }

    @Test
    public void calling_insert_with_invalid_column_name_throws_exception() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("invalid column", "value");

        expectedException.expect(Exception.class);

        classUnderTest.insert("first", cv);
    }

    @Test
    public void get_successfully_retrieves_data() throws Exception {
        insertTestDataIntoTable1();

        Cursor c = classUnderTest.get(new TestQuery() {
            @Override
            public String getQuery() {
                return "SELECT * FROM first";
            }
        });

        assertEquals(2, c.getCount());
    }

    @Test
    public void get_returns_empty_cursor_when_no_data_in_the_database() throws Exception {
        Cursor c = classUnderTest.get(new TestQuery() {
            @Override
            public String getQuery() {
                return "SELECT * FROM first";
            }
        });

        assertNotNull(c);
        assertEquals(0, c.getCount());
    }

    @Test
    public void get_with_invalid_query_throws_exception() throws Exception {
        expectedException.expect(DatabaseException.class);

        classUnderTest.get(new TestQuery() {
            @Override
            public String getQuery() {
                return "invalid query";
            }
        });
    }

    @Test
    public void delete_successfully_deletes_a_single_record() throws Exception {
        insertTestDataIntoTable1();

        classUnderTest.delete(new TestQuery() {
            @Override
            public String getQuery() {
                return "DELETE FROM first WHERE name='lee'";
            }
        });

        SQLiteDatabase db = classUnderTest.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM first", null);

        assertEquals(1, c.getCount());
        c.close();
    }

    @Test
    public void delete_successfully_removes_all_records() throws Exception {
        insertTestDataIntoTable1();

        classUnderTest.delete(new TestQuery() {
            @Override
            public String getQuery() {
                return "DELETE FROM first";
            }
        });

        SQLiteDatabase db = classUnderTest.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM first", null);

        assertEquals(0, c.getCount());
        c.close();
    }

    @Test
    public void update_successfully_updates_record() throws Exception {
        insertTestDataIntoTable1();

        classUnderTest.update(new TestQuery() {
            @Override
            public String getQuery() {
                return "UPDATE first SET name = 'john' WHERE name='lee'";
            }
        });

        SQLiteDatabase db = classUnderTest.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM first WHERE name='john'", null);

        assertEquals(1, c.getCount());
        c.close();
    }

    @Test
    public void update_throws_exception_when_given_invalid_table() throws Exception {
        insertTestDataIntoTable1();

        expectedException.expect(DatabaseException.class);

        classUnderTest.update(new TestQuery() {
            @Override
            public String getQuery() {
                return "UPDATE invalid SET name = 'john' WHERE name='lee'";
            }
        });
    }

    @Test
    public void update_throws_exception_when_given_nonexistant_column() throws Exception {
        insertTestDataIntoTable1();

        expectedException.expect(DatabaseException.class);

        classUnderTest.update(new TestQuery() {
            @Override
            public String getQuery() {
                return "UPDATE first SET non_exist = 'john'";
            }
        });
    }

    @Test
    public void onCreate_throws_exception_if_config_does_not_contain_any_table_create_statements() throws Exception {
        DatabaseConfig invalid = emptyDbConfig();
        SqlDatabase db = new SqlDatabase(RuntimeEnvironment.application, invalid);

        expectedException.expect(RuntimeException.class);
        // force oncreate to be called
        db.getReadableDatabase();
    }


    @Test
    public void onCreate_throws_exception_if_config_create_statements_are_not_valid_sql() throws Exception {
        DatabaseConfig invalid = invalidDbConfig();
        SqlDatabase db = new SqlDatabase(RuntimeEnvironment.application, invalid);

        expectedException.expect(RuntimeException.class);

        db.getReadableDatabase();
    }

    private Cursor getCursor(String rawQuery) {
        SQLiteDatabase db = classUnderTest.getReadableDatabase();
        return db.rawQuery(rawQuery, null);
    }

    private void insertTestDataIntoTable1() throws Exception {
        SQLiteDatabase db = classUnderTest.getWritableDatabase();
        db.insertOrThrow("first", null, createContentValues("lee"));
        db.insertOrThrow("first", null, createContentValues("april"));
    }

    private ContentValues createContentValues(String name) {
        ContentValues cv = new ContentValues(1);
        cv.put("name", name);
        return cv;
    }

    /**
     * Creates two tables.
     *  -- "first":
     *      -- "id" : int primary key
     *      -- "name": text
     *  -- "second":
     *      -- "id" : int primary key
     *      -- "age": int
     */
    private class TestDbConfig implements DatabaseConfig {
        @Override
        public int getVersion() {
            return 1;
        }

        @Override
        public String getFileName() {
            return "sqldatabase_test.db";
        }

        @Override
        public String[] getTableCreationStatements() {
            return new String[]{"CREATE TABLE first(id INTEGER PRIMARY KEY, name TEXT)",
                    "CREATE TABLE second(id INTEGER PRIMARY KEY, age INTEGER)"};
        }

        @Override
        public String[] getTableDeletionStatements() {
            return new String[]{"DROP TABLE IF EXISTS first", "DROP TABLE IF EXISTS second"};
        }
    }

    private DatabaseConfig emptyDbConfig() {
        return new DatabaseConfig() {
            @Override
            public int getVersion() {
                return 1;
            }

            @Override
            public String getFileName() {
                return null;
            }

            @Override
            public String[] getTableCreationStatements() {
                return new String[0];
            }

            @Override
            public String[] getTableDeletionStatements() {
                return new String[0];
            }
        };
    }

    private DatabaseConfig invalidDbConfig() {
        return new TestDbConfig() {
            @Override
            public String[] getTableCreationStatements() {
                return new String[]{"this is invalid sql"};
            }
        };
    }

    private abstract class TestQuery implements Query {
    }

}