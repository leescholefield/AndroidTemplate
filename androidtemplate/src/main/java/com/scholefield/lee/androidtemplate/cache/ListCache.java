package com.scholefield.lee.androidtemplate.cache;

import java.util.List;

/**
 * An extension of {@link Cache} designed for use cases when the stored value is of type list.
 */
public interface ListCache<K, V> extends Cache<K, List<V>> {

    void putSingle(K key, V item);

    void removeSingle(V item);

}
