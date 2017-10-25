package com.scholefield.lee.androidtemplate.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.scholefield.lee.androidtemplate.cache.Cache;
import com.scholefield.lee.androidtemplate.db.query.DeleteQuery;
import com.scholefield.lee.androidtemplate.db.query.SearchQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class CachedDataAccessorTest {

    private CachedDataAccessor<TestObject> classUnderTest;

    // dependencies
    private Database database;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        database = new SqlDatabase(RuntimeEnvironment.application, new TestDbConfig());
        classUnderTest = new CachedDataAccessor<>(database);
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
    public void put_inserts_obj_into_cache_when_list_does_not_exist() throws Exception {
        classUnderTest.put(new TestObject("name"), "foo");

        Cache<String, List<TestObject>> cache = classUnderTest.getCache();

        assertNotNull(cache.get("foo"));
    }

    @Test
    public void put_inserts_obj_into_cache_when_items_already_present() throws Exception {
        Cache<String, List<TestObject>> cache = classUnderTest.getCache();
        List<TestObject> existing = new ArrayList<>();
        existing.add(new TestObject("existing"));
        cache.put("foo", existing);

        classUnderTest.put(new TestObject("inserted"), "foo");

        assertEquals(2, cache.get("foo").size());
    }

    @Test
    public void put_throws_runtime_exception_when_default_writer_not_set() throws Exception {
        CachedDataAccessor<TestObject> accessor = new CachedDataAccessor<>(database);
        expectedException.expect(RuntimeException.class);

        accessor.put(new TestObject("name"), "foo");
    }

    @Test
    public void get_returns_from_cache_when_forceUpdate_is_false() throws Exception {
        List<TestObject> cached = new ArrayList<>();
        cached.add(new TestObject("name"));
        classUnderTest.getCache().put("SELECT * FROM foo", cached);

        List<TestObject> result = classUnderTest.get(new SearchQuery("foo"), false);

        assertEquals(cached, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void get_does_not_check_cache_when_forceUpdate_is_true() throws Exception {
        Cache mockedCache = mock(Cache.class);
        classUnderTest.setCache(mockedCache);

        classUnderTest.get(new SearchQuery("foo"), true);

        verify(mockedCache, never()).get(ArgumentMatchers.any());
    }

    @Test
    public void get_updates_cache_when_database_called() throws Exception {
        ContentValues cv = new ContentValues();
        cv.put("name", "new name");
        database.insert("foo", cv);

        classUnderTest.get(new SearchQuery("foo"), false);

        assertEquals(1, classUnderTest.getCache().get("SELECT * FROM foo").size());
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

    private class TestObject {
        private int id;
        private String name;

        TestObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        TestObject(String name) {
            this.id = -1;
            this.name = name;
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

    private class DataReaderImp implements DataReader<TestObject> {

        @Override
        public TestObject fromCursor(Cursor data) {
            String name = data.getString(data.getColumnIndexOrThrow("name"));
            int id = data.getInt(data.getColumnIndexOrThrow("id"));
            return new TestObject(id, name);
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