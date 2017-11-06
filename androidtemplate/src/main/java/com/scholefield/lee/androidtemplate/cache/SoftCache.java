package com.scholefield.lee.androidtemplate.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * An in-memory cache that stores each item as a {@code SoftReference}. Although getting/putting to the cache is synchronized,
 * you should be careful about modifying any stored item in-case it is being used by another thread.
 *
 * This implementation will evict the least-recently used item from the cache when the total number of items exceeds
 * {@link #maxSize}, or when the item has been garbage-collected. A size of an item is determined by a call to {@link #itemSize}.
 * The default behaviour is to return 1 for all non-collection types and the number of items in the collection for collection types.
 */
public class SoftCache<K, V> implements Cache<K, V> {

    /**
     * Stores the item as a {@link SoftValue}
     */
    private LinkedHashMap<K, SoftValue> itemMap;

    /**
     * Used to remove items that have been garbage collected.
     */
    private ReferenceQueue<V> referenceQueue = new ReferenceQueue<>();

    /**
     * Maintains key order so the least-recently used entry can be removed when {@code maxSize} is reached.
     */
    private LinkedList<K> keyInsertionOrder = new LinkedList<>();

    /**
     * Maximum number of items that can be stored in the {@code itemMap}.
     */
    private int maxSize;

    /**
     * Current number of items in the {@code itemMap}.
     */
    private int currentSize = 0;

    /**
     * Removed values that have been gc'ed from the {@link #itemMap}.
     */
    private Thread cleanUpThread = new CleanupThread();

    /**
     * Public constructor. Instantiates the {@link #itemMap} and sets the {@code maxSize}.
     */
    public SoftCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be greater than 0");
        }

        this.maxSize = maxSize;

        itemMap = new LinkedHashMap<>();
        cleanUpThread.start();
    }

    /**
     * Retrieves an item for the specified {@code key} or {@code null} if there is no such key.
     */
    @Override
    public V get(K key) {
        if (key == null)
            throw new NullPointerException("key == null");

        synchronized (this) {
            SoftValue sr = itemMap.get(key);

            // key not found
            if (sr == null) {
                return null;
            }

            V item = sr.get();
            // key found but item been gc
            if (item == null) {
                itemMap.remove(key);
                currentSize -= sr.size();
            } else {
                moveToEndOfKeyInsertionOrder(key);
            }

            return item;
        }
    }

    /**
     * Since the cache will delete the first element in {@link #keyInsertionOrder} we need to move the most recently accessed
     * keys to the end of the list.
     */
    private void moveToEndOfKeyInsertionOrder(K key) {
        boolean removed = keyInsertionOrder.remove(key);
        if (!removed) {
            throw new IllegalArgumentException("key is not in keyInsertOrder");
        }
        keyInsertionOrder.add(key);
    }

    /**
     * Appends the {@code key} to {@link #keyInsertionOrder}.
     *
     * In order to remain consistent with {@link #itemMap} if there was a matching key saved to keyInsertionOrder it will
     * be removed before adding the new key.
     */
    private void saveToKeyInsertionOrder(K key) {
        if (keyInsertionOrder.contains(key)) {
            keyInsertionOrder.remove(key);
        }

        keyInsertionOrder.add(key);
    }

    /**
     * Stores the {@code item} in the cache under the given {@code key}.
     */
    @Override
    public void put(K key, V item) {

        if (key == null || item == null)
            throw new IllegalArgumentException("(Key == null || item == null)");

        synchronized (this) {

            SoftValue sv = createSoftValue(key, item);
            int itemSize = sv.size();

            if(itemSize > maxSize) {
                throw new IllegalArgumentException("size of inserted item is greater than maxSize");
            }

            while(currentSize + itemSize > maxSize) {
                K oldest = keyInsertionOrder.poll();
                removeItem(oldest);
            }

            itemMap.put(key, sv);
            currentSize += itemSize;
            saveToKeyInsertionOrder(key);
        }
    }

    /**
     * Clears the cache of all of its items
     */
    @Override
    public void removeAll() {
        synchronized (this) {
            itemMap.clear();
            keyInsertionOrder.clear();
            currentSize = 0;
        }
    }

    /**
     * Removes the item associated with the given {@code key} from the cache.
     */
    @Override
    public void remove(K key) {
        synchronized (this) {
            removeItem(key);
        }
    }

    /**
     * Removes an item from the underlying map, its key from the key-list and decrement {@code #currentSize} by item size.
     */
    private void removeItem(SoftValue item) {
        K key = item.getKey();
        itemMap.remove(key);
        keyInsertionOrder.remove(key);

        int size = item.size();
        currentSize -= size;
    }

    public void removeItem(K key) {
        SoftValue sv = itemMap.get(key);
        if (sv != null) {
            removeItem(sv);
        }
    }

    /**
     * Returns the number of items in the underlying map.
     */
    @Override
    public synchronized int size() {
        return currentSize;
    }

    /**
     * Returns the maximum number of items that the cache can hold.
     */
    @Override
    public int maxSize() {
        return maxSize;
    }

    /**
     * Creates a new {@link SoftValue}. Package-private so it can be tested.
     *
     * @param key key used to save the item in the {@link #itemMap}
     * @param value itemMap value.
     * @return a new {@code SoftValue} instance.
     */
    SoftValue createSoftValue(K key, V value) {
        int size = itemSize(value);
        return new SoftValue(key, value, size, referenceQueue);
    }

    /**
     * Returns the {@link #itemMap}.
     *
     * Used for unit testing.
     */
    Map<K, SoftValue> getMap() {
        return itemMap;
    }

    /**
     * Returns the {@link #keyInsertionOrder}.
     *
     * Used for unit testing.
     */
    List<K> getKeyInsertionOrder() {
        return keyInsertionOrder;
    }

    /**
     * Returns the size of {@code item}. If item is an instance of Collection or Map, this will return the total number of
     * items. For any other type this will return 1.
     */
    int itemSize(V item) {
        int size = 1;
        if (item instanceof Collection) {
            size = ((Collection) item).size();
        } else if (item instanceof Map) {
            size = ((Map) item).values().size();
        }

        return size;
    }

    /**
     * Extension of {@link SoftReference<V>} that also stores the key it's saved under in the map. This is to make it easier
     * to remove from the map when processing the {@code ReferenceQueue}.
     */
    class SoftValue extends SoftReference<V> {

        /**
         * Key for this value in the {@link #itemMap}.
         */
        private K key;

        private int size;

        SoftValue(K key, V ref, int size, ReferenceQueue<? super V> queue) {
            super(ref, queue);
            this.key = key;
            this.size = size;
        }

        K getKey() {
            return key;
        }

        int size() {return size;}
    }

    /**
     * Removes garbage collected values from the {@link #itemMap}.
     */
    private class CleanupThread extends Thread {

        /**
         * Used as name for {@link Thread#setName(String)}
         */
        final String TAG = CleanupThread.class.getName();

        /**
         * Constructor. Sets various Thread options.
         */
        CleanupThread() {
            setPriority(Thread.MAX_PRIORITY);
            setName(TAG);
            setDaemon(true);
        }

        /**
         * Waits for a value to be added to the {@link #referenceQueue} and then calls the {@link SoftCache#remove} method.
         */
        @SuppressWarnings({"unchecked", "InfiniteLoopStatement"}) // unchecked: guaranteed to be a SoftValue instance
        @Override
        public void run() {
            while (true) {
                try {
                    // blocks until it gets a new value
                    SoftValue sv = (SoftValue) referenceQueue.remove();

                    remove(sv.getKey());
                } catch (InterruptedException e) {
                    // should never happen
                    throw new Error("CleanUpThread interrupted", e);
                }
            }
        }
    }
}
