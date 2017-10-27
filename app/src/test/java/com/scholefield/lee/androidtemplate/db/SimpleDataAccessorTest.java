package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.scholefield.lee.androidtemplate.db.query.DeleteQuery;
import com.scholefield.lee.androidtemplate.db.query.SearchQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class SimpleDataAccessorTest {

    private DataAccessor<TestObject> classUnderTest;
    private Database database;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        database = new SqlDatabase(RuntimeEnvironment.application, new TestDbConfig());
        classUnderTest = new SimpleDataAccessor<>(database);
        classUnderTest.setDefaultReader(new DataReaderImp());
        classUnderTest.setDefaultWriter(new DataWriterImp());
    }

    @After
    public void cleanUp() throws Exception {
        database.delete(new DeleteQuery("foo"));
    }

    @Test
    public void put_inserts_data_into_database() throws Exception {
        classUnderTest.put(new TestObject("name"), "foo");

        List<TestObject> inDb = getCurrentDbContents();

        assertEquals(1, inDb.size());
    }

    @Test
    public void put_throws_runtime_exception_on_database_error() throws Exception {
        expectedException.expect(RuntimeException.class);

        // invalid table name should throw DatabaseException
        classUnderTest.put(new TestObject("name"), "invalidTable");
    }

    @Test
    public void put_throws_exception_when_default_writer_not_set() throws Exception {
        DataAccessor<TestObject> accessor = new SimpleDataAccessor<>(database);

        expectedException.expect(NullPointerException.class);

        accessor.put(new TestObject(1, "name"), "foo");
    }

    @Test
    public void get_successfully_gets_data_from_database() throws Exception {
        insertDataIntoDatabase(new TestObject("first"));
        insertDataIntoDatabase(new TestObject("second"));

        List<TestObject> current = classUnderTest.get(new SearchQuery("foo"), true);

        assertEquals(2, current.size());
    }

    @Test
    public void get_throws_exception_when_default_reader_not_set() throws Exception {
        DataAccessor<TestObject> accessor = new SimpleDataAccessor<>(database);

        expectedException.expect(NullPointerException.class);

        accessor.get(new SearchQuery("foo"), false);
    }

    private List<TestObject> getCurrentDbContents() throws Exception {
        Cursor c = database.get(new SearchQuery("foo"));
        List<TestObject> result = new ArrayList<>();

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String name = c.getString(c.getColumnIndexOrThrow("name"));
            result.add(new TestObject(id, name));
        }

        return result;
    }

    private void insertDataIntoDatabase(TestObject object) throws Exception {
        DataWriter<TestObject> writer = new DataWriterImp();
        ContentValues cv = writer.toContentValues(object);
        database.insert("foo", cv);
    }


    private class DataReaderImp implements DataReader<TestObject> {

        @Override
        public TestObject fromCursor(Cursor data) {
            String name = data.getString(data.getColumnIndexOrThrow("name"));
            int id = data.getInt(data.getColumnIndexOrThrow("id"));
            return new TestObject(id, name);
        }
    }

    private class DataWriterImp implements DataWriter<TestObject> {

        @Override
        public ContentValues toContentValues(TestObject obj) {
            ContentValues cv = new ContentValues(1);
            cv.put("name", obj.name);
            return cv;
        }
    }

    private class TestObject {
        private String name;
        private int id;

        TestObject(int id, String value) {
            this.name = value;
            this.id = id;
        }

        TestObject(String value) {
            this.name = value;
        }
    }

    /**
     * This will create a table named foo with two columns: id (integer) and name (string).
     */
    private class TestDbConfig implements DatabaseConfig {

        @Override
        public int getVersion() {
            return 1;
        }

        @Override
        public String getFileName() {
            return "dataAccessor_test.db";
        }

        @Override
        public String[] getTableCreationStatements() {
            return new String[]{"CREATE TABLE foo(id INTEGER PRIMARY KEY, name TEXT)"};
        }

        @Override
        public String[] getTableDeletionStatements() {
            return new String[]{"DROP TABLE IF EXISTS foo"};
        }
    }


}