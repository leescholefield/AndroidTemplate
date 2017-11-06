package com.scholefield.lee.androidtemplate.cache;

import java.util.List;

/**
 * An extension of {@link Cache} for when the cached item is of type list. This provides easy methods for removing/ inserting
 * individual items in the list.
 */
public interface ListCache<K, V> extends Cache<K, List<V>> {

    void putSingle(K key, V item);

    void removeSingle(V item);

}
