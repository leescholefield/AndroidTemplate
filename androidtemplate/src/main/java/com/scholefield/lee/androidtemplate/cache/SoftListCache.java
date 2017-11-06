package com.scholefield.lee.androidtemplate.cache;

import java.util.*;

/**
 * An extension of {@link Cache} for when the stored item is of type list. This provides easy methods for removing/ inserting
 * an item into a cached list.
 */
public class SoftListCache<K, V> implements ListCache<K, V> {

    private Map<K, List<V>> dataSet;
    private LinkedList<K> keyInsertionOrder = new LinkedList<>();

    private int maxSize;
    private int currentSize;

    public SoftListCache(int maxSize) {
        this.maxSize = maxSize;
        this.currentSize = 0;

        dataSet = new HashMap<>();
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
                int previousSize = dataSet.get(key).size();
                currentSize -= previousSize;
            }

            while(currentSize + item.size() > maxSize) {
                K oldest = keyInsertionOrder.poll();
                removeItem(oldest);
            }

            dataSet.put(key, item);
            saveToKeyInsertionOrder(key);
            currentSize += item.size();
        }

    }

    @Override
    public void putSingle(K key, V item) {
        if (key == null || item == null) {
            throw new NullPointerException("key == null || item == null");
        }

        synchronized (this) {
            List<V> insertedList = new ArrayList<>();
            List<V> previousList = dataSet.get(key);

            if (previousList != null) {
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
        List<V> removed = dataSet.remove(key);
        currentSize -= removed.size();
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
            List<V> list = dataSet.get(key);
            if (list != null) {
                saveToKeyInsertionOrder(key);
            }
            return list;
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

    protected Map<K, List<V>> getDataSet() {
        return dataSet;
    }

    List<K> getKeyInsertionOrder() {
        return keyInsertionOrder;
    }
}
