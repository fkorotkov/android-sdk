package com.kinvey.nativejava;


import com.google.api.client.json.GenericJson;
import com.kinvey.java.cache.ICache;
import com.kinvey.java.cache.ICacheManager;

class CacheManager implements ICacheManager {

    @Override
    public <T extends GenericJson> ICache<T> getCache(String collection, Class<T> collectionItemClass, Long ttl) {
        return null;
    }

    @Override
    public void clear() {

    }
}
