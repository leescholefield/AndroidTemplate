package com.scholefield.lee.androidtemplate.cache;

/**
 * An in-memory cache for storing recently used items.
 *
 * @param <K> key type dataSet are stored under.
 * @param <T> type of dataSet stored in the cache.
 */
public interface Cache<K, T> {

    /**
     * Retrieves an item for the specified {@code key} or {@code null} if there is no such key.
     */
    T get(K key);

    /**
     * Stores the {@code item} in the cache under the given {@code key}.
     */
    void put(K key, T item);

    /**
     * Clears the cache of all of its dataSet.
     */
    void removeAll();

    /**
     * Removes the item associated with the given {@code key} from the cache.
     */
    void remove(K key);

    /**
     * Returns the current size of the cache in bytes.
     */
    int size();

    /**
     * Returns the weight to give this item when saving it to the cache.
     */
    int itemWeight(T item);

    /**
     * Returns the maximum size in bytes that the cache can hold.
     */
    int maxSize();
}
