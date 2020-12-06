package com.zy.github.multiple.cache;


/**
 * @author zy 2020/12/6
 */
public interface CachedValueRebuilder<K, V> extends CacheDecorationHandler {

    V rebuild(K key, V value);
}
