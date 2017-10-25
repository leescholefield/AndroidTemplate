package com.scholefield.lee.androidtemplate.cache;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.ref.SoftReference;

import static org.junit.Assert.*;

/**
 *
 */
public class SoftCacheTest {

    private SoftCacheTestImp classUnderTest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        classUnderTest = new SoftCacheTestImp();
    }

    @Test
    public void get_returns_value_when_present() {
        insertValueIntoMap("first", "first value");

        assertEquals("first value", classUnderTest.get("first"));
    }

    @Test
    public void get_returns_null_when_key_not_present() throws Exception {
        assertNull(classUnderTest.get("not exist"));
    }

    @Test
    public void get_throws_runtime_when_key_null() throws Exception {
        expectedException.expect(RuntimeException.class);

        classUnderTest.get(null);
    }

    @Test
    public void get_places_key_at_end_of_keyInsertionOrder() throws Exception {
        insertValueIntoMap("first", "first value");
        insertValueIntoMap("second", "second value");

        assertEquals("first", classUnderTest.getKeyInsertionOrder().get(0));

        classUnderTest.get("first");

        assertEquals(2, classUnderTest.getKeyInsertionOrder().size());
        assertEquals("second", classUnderTest.getKeyInsertionOrder().get(0));
    }

    @Test
    public void put_saves_to_map() throws Exception {
        classUnderTest.put("key", "value");

        assertEquals(1, classUnderTest.size());
        assertNotNull(classUnderTest.get("key"));
    }

    @Test
    public void put_saves_to_keyInsertionOrder() throws Exception {
        classUnderTest.put("key", "value");

        assertEquals(1, classUnderTest.getKeyInsertionOrder().size());
        assertNotNull(classUnderTest.getKeyInsertionOrder().contains("key"));
    }

    @Test
    public void put_removes_oldest_entry_when_max_size_reached() throws Exception {
        classUnderTest.put("first", "first value");
        classUnderTest.put("second", "second value");

        // next put should remove "first"
        classUnderTest.put("max", "max value");

        assertEquals(2, classUnderTest.size());
        assertNull(classUnderTest.get("first"));
    }

    @Test
    public void remove_removes_item() throws Exception {
        classUnderTest.put("key", "value");

        classUnderTest.remove("key");

        assertEquals(0, classUnderTest.size());
    }

    @Test
    public void remove_removes_item_from_keyInsertionOrder() throws Exception {
        classUnderTest.put("key", "value");

        classUnderTest.remove("key");

        assertEquals(0, classUnderTest.getKeyInsertionOrder().size());
    }

    @Test
    public void removeAll_empties_map() throws Exception {
        classUnderTest.put("first", "value");
        classUnderTest.put("second", "value");

        classUnderTest.removeAll();

        assertEquals(0, classUnderTest.size());
    }

    @Test
    public void removeAll_empties_keyInsertionOrder() throws Exception {
        classUnderTest.put("first", "value");
        classUnderTest.put("second", "value");

        classUnderTest.removeAll();

        assertEquals(0, classUnderTest.getKeyInsertionOrder().size());
    }

    @Test
    public void keyInsertionOrder_does_not_allow_duplicate_value() throws Exception {
        classUnderTest.put("key", "value");
        classUnderTest.put("key", "value");

        assertEquals(1, classUnderTest.getKeyInsertionOrder().size());
    }


    /**
     * Note, this will sleep for 1 second to allow time for the clean up thread to execute.
     */
    @Ignore
    @Test
    public void cleanUpThread_removes_enqueued_items_from_map_and_keyInsertionOrder() throws Exception {
        classUnderTest.put("first", "first");
        classUnderTest.put("second", "second");

        classUnderTest.enqueueItem("first");

        // delay for 1 seconds to allow CleanupThread time to execute
        Thread.sleep(1000);

        assertEquals(1, classUnderTest.size());
        assertEquals(1, classUnderTest.getKeyInsertionOrder().size());
    }


    /**
     * Helper method for inserting into the SoftCache map directly, bypassing {@link SoftCache#put}.
     */
    private void insertValueIntoMap(String key, String value) {
        classUnderTest.getMap().put(key, classUnderTest.createSoftValue(key, value));
        classUnderTest.getKeyInsertionOrder().add(key);
    }

    private class SoftCacheTestImp extends SoftCache<String, String> {

        private SoftCacheTestImp() {
            super(2);
        }

        /**
         * Enqueues the item matching the {@code key} in the {@link #referenceQueue}.
         */
        private void enqueueItem(String key) {
            SoftReference<String> reference = getMap().get(key);
            reference.enqueue();
        }

    }

}