package com.scholefield.lee.androidtemplate.cache;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class SoftListCacheTest {

    private SoftListCache<String, String> classUnderTest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        classUnderTest = new SoftListCache<>(3);
    }

    @Test
    public void remove_removes_items_from_data_set() throws Exception {
        insertTestValuesIntoDataSet("toRemove");

        classUnderTest.remove("toRemove");

        assertNull(classUnderTest.get("toRemove"));
    }

    @Test
    public void remove_removes_key_from_keyInsertionOrder() throws Exception {
        classUnderTest.putSingle("key", "value");

        classUnderTest.remove("key");

        assertEquals(0, classUnderTest.getKeyInsertionOrder().size());
    }

    @Test
    public void remove_decreases_currentSize_by_size_of_removed_list() throws Exception {
        insertTestValuesIntoDataSet("toRemove");

        classUnderTest.remove("toRemove");

        assertEquals(-2, classUnderTest.size());
    }

    @Test
    public void removeAll_clears_cache() throws Exception {
        insertTestValuesIntoDataSet("first");
        insertTestValuesIntoDataSet("second");

        classUnderTest.removeAll();

        assertTrue(classUnderTest.getDataSet().isEmpty());
    }

    @Test
    public void removeAll_resets_currentSize_to_0() throws Exception {
        insertTestValuesIntoDataSet("first");
        insertTestValuesIntoDataSet("second");

        classUnderTest.removeAll();

        assertEquals(0, classUnderTest.size());
    }

    @Test
    public void removeAll_clears_keyInsertionOrder() throws Exception {
        insertTestValuesIntoDataSet("key");

        classUnderTest.removeAll();

        assertEquals(0, classUnderTest.getKeyInsertionOrder().size());
    }

    @Test
    public void removeSingle_successfully_removes_item() throws Exception {
        insertTestValuesIntoDataSet("key");

        classUnderTest.removeSingle("first");

        assertFalse(classUnderTest.get("key").contains("first"));
    }

    @Test
    public void removeSingle_decreases_currentSize_by_1_when_removed() throws Exception {
        insertTestValuesIntoDataSet("key");
        int currentSize = classUnderTest.size();

        classUnderTest.removeSingle("second");

        assertEquals(currentSize - 1, classUnderTest.size());
    }

    @Test
    public void removeSingle_does_nothing_when_given_nonexistant_item() throws Exception {
        insertTestValuesIntoDataSet("key");
        int currentSize = classUnderTest.size();

        classUnderTest.removeSingle("not-exist");

        assertTrue(classUnderTest.size() == currentSize);
    }

    @Test
    public void put_successfully_puts_inserts_into_cache() throws Exception {
        List<String> inserted = createList("first", "second");
        classUnderTest.put("key", inserted);

        assertNotNull(classUnderTest.get("key"));
    }

    @Test
    public void put_appends_to_keyInsertionOrder() throws Exception {
        classUnderTest.put("key", createList("value"));

        assertEquals(1, classUnderTest.getKeyInsertionOrder().size());
    }

    @Test
    public void put_increases_cache_size() throws Exception {
        List<String> inserted = createList("first", "second");
        classUnderTest.put("key", inserted);

        assertEquals(2, classUnderTest.size());
    }

    @Test
    public void put_replaces_previous_mapping() throws Exception {
        List<String> first = createList("first", "second", "third");
        classUnderTest.put("key", first);

        List<String> second = createList("first");
        classUnderTest.put("key", second);

        assertEquals(1, classUnderTest.get("key").size());
    }

    @Test
    public void put_recalculates_cache_size() throws Exception {
        List<String> first = createList("first", "second", "third");
        classUnderTest.put("key", first);
        assertEquals(3, classUnderTest.size());

        List<String> second = createList("first");
        classUnderTest.put("key", second);

        assertEquals(1, classUnderTest.size());
    }

    @Test
    public void put_removes_oldest_entry_when_exceeds_maxSize() throws Exception {
        List<String> first = createList("first", "second");
        classUnderTest.put("first", first);

        List<String> second = createList("first", "second");
        classUnderTest.put("second", second);

        assertNull(classUnderTest.get("first"));
        assertEquals(2, classUnderTest.size());
    }

    @Test
    public void put_throws_exception_when_key_null() throws Exception {
        expectedException.expect(NullPointerException.class);

        classUnderTest.put(null, createList("first"));
    }

    @Test
    public void put_throws_exception_when_inserted_list_exceeds_max_size() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        classUnderTest.put("key", createList("f", "s", "t", "o"));
    }

    @Test
    public void putSingle_adds_item_to_cache() throws Exception {
        classUnderTest.putSingle("key", "value");

        assertNotNull(classUnderTest.get("key"));
    }

    @Test
    public void putSingle_adds_key_to_itemInsertionOrder() throws Exception {
        classUnderTest.putSingle("key", "value");

        assertEquals(1, classUnderTest.getKeyInsertionOrder().size());
    }

    @Test
    public void putSingle_appends_item_to_previous_list() throws Exception {
        classUnderTest.put("key", createList("first", "second"));

        classUnderTest.putSingle("key", "third");

        assertEquals(3, classUnderTest.get("key").size());
        assertEquals(1, classUnderTest.getDataSet().keySet().size());
    }

    @Test
    public void putSingle_does_not_create_duplicate_keys_in_keyInsertionOrder() throws Exception {
        classUnderTest.putSingle("key", "value");
        classUnderTest.putSingle("key", "second value");

        assertEquals(1, classUnderTest.getKeyInsertionOrder().size());
    }

    @Test
    public void putSingle_throws_exception_when_key_null() throws Exception {
        expectedException.expect(NullPointerException.class);

        classUnderTest.putSingle(null, "value");
    }

    @Test
    public void get_returns_list() throws Exception {
        insertTestValuesIntoDataSet("key");

        assertNotNull(classUnderTest.get("key"));
    }

    @Test
    public void get_moves_key_to_end_of_keyInsertionOrder() throws Exception {
        classUnderTest.putSingle("key", "item");
        classUnderTest.putSingle("second", "item");

        classUnderTest.get("key");

        assertEquals("key", classUnderTest.getKeyInsertionOrder().get(1));
    }

    @Test
    public void get_returns_null_when_cache_empty() throws Exception {
        classUnderTest.get("key");
        assertEquals(0, classUnderTest.getKeyInsertionOrder().size());
    }

    /**
     * Note, this will sleep for 1 second to allow time for the clean up thread to execute.
     */
    @Ignore
    @Test
    public void cleanUpThread_removes_enqueued_items_from_map_and_keyInsertionOrder() throws Exception {
        ListCacheTempImp<String, String> classUnderTest = new ListCacheTempImp<>(3);
        classUnderTest.put("first", createList("first"));
        classUnderTest.put("second", createList("second"));

        classUnderTest.enqueueItem("first");

        // delay for 1 seconds to allow CleanupThread time to execute
        Thread.sleep(1000);

        assertEquals(1, classUnderTest.size());
        assertEquals(1, classUnderTest.getKeyInsertionOrder().size());
    }

    private List<String> createList(String ... values) {
        List<String> l = new ArrayList<>(values.length);
        l.addAll(Arrays.asList(values));
        return l;
    }

    private void insertTestValuesIntoDataSet(String key) {
        List<String> toInsert = createList("first", "second");

        SoftListCache<String, String>.SoftValue sv = classUnderTest.createSoftValue(key, toInsert);
        classUnderTest.getDataSet().put(key, sv);

        classUnderTest.getKeyInsertionOrder().add(key);
    }

    private class ListCacheTempImp<K, V> extends SoftListCache<K, V> {

        ListCacheTempImp(int maxSize) {
            super(maxSize);
        }

        private void enqueueItem(K key) {
            SoftReference reference = getDataSet().get(key);
            reference.enqueue();
        }
    }


}