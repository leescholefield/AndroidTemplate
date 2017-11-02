package com.scholefield.lee.androidtemplate.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SoftListCache<K, V> implements ListCache<K, V> {

    private Map<K, List<V>> dataSet;

    private int maxSize;
    private int currentSize;

    public SoftListCache(int maxSize) {
        this.maxSize = maxSize;
        this.currentSize = 0;

        dataSet = new HashMap<>();
    }

    @Override
    public void putSingle(K key, V item) {

    }

    @Override
    public void removeSingle(V item) {
        synchronized (this) {
            // will have to check each value in cache
            List<V> removeFrom = null;
            for (Map.Entry<K, List<V>> kListEntry : dataSet.entrySet()) {
                List<V> list = kListEntry.getValue();
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
     * Retrieves an item for the specified {@code key} or {@code null} if there is no such key.
     *
     * @param key
     */
    @Override
    public List<V> get(K key) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }

        synchronized (this) {
            return dataSet.get(key);
        }
    }

    /**
     * Stores the {@code item} in the cache under the given {@code key}.
     *
     * @param key
     * @param item
     */
    @Override
    public void put(K key, List<V> item) {

    }

    /**
     * Clears the cache of all of its dataSet.
     */
    @Override
    public void removeAll() {
        synchronized (this) {
            dataSet.clear();
            currentSize = 0;
        }
    }

    /**
     * Removes all the items associated with the given {@code key} from the cache.
     */
    @Override
    public void remove(K key) {
        synchronized (this) {
            List<V> removed = dataSet.remove(key);
            currentSize = currentSize - removed.size();
        }
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

    protected Map<K, List<V>> getDataSet() {
        return dataSet;
    }
}
