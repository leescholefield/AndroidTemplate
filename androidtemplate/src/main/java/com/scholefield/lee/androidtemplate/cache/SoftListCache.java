package com.scholefield.lee.androidtemplate.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * An extension of {@link Cache} for when the stored item is of type list. This provides easy methods for removing/ inserting
 * an item into a cached list.
 */
public class SoftListCache<K, V> implements ListCache<K, V> {

    private Map<K, SoftValue> dataSet;

    /**
     * The last-used key should be moved to the end of the list.
     */
    private LinkedList<K> keyInsertionOrder = new LinkedList<>();

    private int maxSize;
    private int currentSize = 0;

    private Thread cleanUpThread = new CleanUpThread();

    private ReferenceQueue<List<V>> referenceQueue = new ReferenceQueue<>();

    /**
     * @param initialSize the initial size of the created Map. If it is too-small this will have a performance penalty when it
     *                    is increased. Too large and it will waste memory.
     * @param maxSize maximum number of items that can be stored in the cache.
     */
    public SoftListCache(int initialSize, int maxSize) {
        this.maxSize = maxSize;

        dataSet = new HashMap<>(initialSize);
        cleanUpThread.start();
    }

    /**
     * Constructor. The initial size of the internal map will be set to 1/4 of {@code maxSize}.
     *
     * @param maxSize maximum number of items that can be stored in the cache.
     */
    public SoftListCache(int maxSize) {
        this(maxSize / 4, maxSize);
    }

    /**
     * Stores the {@code item} in the cache under the given {@code key}.
     *
     * Note, this will replace any previous mapping {@code key} had.
     */
    @Override
    public void put(K key, List<V> item) {

        if (key == null || item == null) {
            throw new NullPointerException("key == null || item == null");
        }

        if (item.size() > maxSize) {
            throw new IllegalArgumentException("item size exceeds maxSize");
        }

        synchronized (this) {

            if (dataSet.containsKey(key)) {
                int previousSize = dataSet.get(key).getSize();
                currentSize -= previousSize;
            }

            while(currentSize + item.size() > maxSize) {
                K oldest = keyInsertionOrder.poll();
                removeItem(oldest);
            }

            SoftValue sv = createSoftValue(key, item);
            int size = sv.getSize();

            dataSet.put(key, sv);
            saveToKeyInsertionOrder(key);
            currentSize += size;
        }

    }

    @Override
    public void putSingle(K key, V item) {
        if (key == null || item == null) {
            throw new NullPointerException("key == null || item == null");
        }

        synchronized (this) {
            List<V> insertedList = new ArrayList<>();

            SoftValue sv = dataSet.get(key);
            if (sv != null) {
                List<V> previousList = sv.get();
                insertedList.addAll(previousList);
            }

            insertedList.add(item);

            put(key, insertedList);
        }
    }

    /**
     * Removes the given {@code item} from the list in the dataSet.
     */
    @Override
    public void removeSingle(V item) {
        synchronized (this) {
            // will have to check each value in cache
            List<V> removeFrom = null;
            for (Map.Entry<K, SoftValue> kListEntry : dataSet.entrySet()) {
                List<V> list = kListEntry.getValue().get();

                if (list.contains(item)) {
                    // avoids any unexpected behaviour from modifying map in the for loop.
                    removeFrom = list;
                    break;
                }
            }

            if (removeFrom != null) {
                removeFrom.remove(item);
                currentSize -= 1;
            }
        }
    }

    /**
     * Clears the cache of all of its dataSet.
     */
    @Override
    public void removeAll() {
        synchronized (this) {
            dataSet.clear();
            currentSize = 0;
            keyInsertionOrder.clear();
        }
    }

    /**
     * Removes all the items associated with the given {@code key} from the cache.
     */
    @Override
    public void remove(K key) {
        synchronized (this) {
            removeItem(key);
        }
    }

    private void removeItem(K key) {
        SoftValue removed = dataSet.remove(key);
        currentSize -= removed.getSize();
        keyInsertionOrder.remove(key);
    }

    /**
     * Retrieves an item for the specified {@code key} or {@code null} if there is no such key.
     */
    @Override
    public List<V> get(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }

        synchronized (this) {
            SoftValue sv = dataSet.get(key);
            if (sv != null) {
                saveToKeyInsertionOrder(key);
                return sv.get();
            } else {
                return null;
            }
        }
    }

    /**
     * Appends the {@code key} to {@link #keyInsertionOrder}.
     *
     * In order to remain consistent with {@link #dataSet} if there was a matching key saved to keyInsertionOrder it will
     * be removed before adding the new key.
     */
    private void saveToKeyInsertionOrder(K key) {
        if (keyInsertionOrder.contains(key)) {
            keyInsertionOrder.remove(key);
        }

        keyInsertionOrder.add(key);
    }

    /**
     * Returns the current size of the cache in bytes.
     */
    @Override
    public int size() {
        return currentSize;
    }

    /**
     * Returns the maximum size in bytes that the cache can hold.
     */
    @Override
    public int maxSize() {
        return maxSize;
    }

    SoftValue createSoftValue(K key, List<V> value) {
        int size = value.size();
        return new SoftValue(key, value, size, referenceQueue);
    }

    /**
     * Extension of {@link SoftReference<V>} that also stores the key it's saved under in the map. This is to make it easier
     * to remove from the map when processing the {@code ReferenceQueue}.
     */
    class SoftValue extends SoftReference<List<V>> {

        private K key;
        private int size;

        SoftValue(K key, List<V> ref, int size, ReferenceQueue<? super List<V>> queue) {
            super(ref, queue);
            this.key = key;
            this.size = size;
        }

        K getKey() {
            return key;
        }

        int getSize() {
            return size;
        }
    }

    /**
     * Removes garbage collected value from the {@link #dataSet}.
     */
    private class CleanUpThread extends Thread {

        final String TAG = CleanUpThread.class.getName();

        CleanUpThread() {
            setPriority(Thread.MAX_PRIORITY);
            setName(TAG);
            setDaemon(true);
        }

        @SuppressWarnings({"unchecked", "InfiniteLoopStatement"})
        @Override
        public void run() {
            while(true) {
                try {
                    // blocks until it gets a new value
                    SoftValue val = (SoftValue)referenceQueue.remove();
                    remove(val.getKey());
                } catch (InterruptedException e) {
                    // should never happen
                    throw new Error("CleanUpThread interrupted", e);
                }
            }
        }
    }

    /**
     * Used for testing.
     */
    Map<K, SoftValue> getDataSet() {
        return dataSet;
    }

    /**
     * Used for testing.
     */
    List<K> getKeyInsertionOrder() {
        return keyInsertionOrder;
    }
}
