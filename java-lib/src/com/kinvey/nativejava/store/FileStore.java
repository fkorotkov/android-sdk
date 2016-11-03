package com.kinvey.nativejava.store;

import com.kinvey.java.cache.ICacheManager;
import com.kinvey.java.network.NetworkFileManager;
import com.kinvey.java.store.BaseFileStore;
import com.kinvey.java.store.StoreType;

/**
 * Created on 03.11.2016.
 */

public class FileStore extends BaseFileStore {

    /**
     * File store constructor
     *
     * @param networkFileManager manager for the network file requests
     * @param cacheManager       manager for local cache file request
     * @param ttl                Time to Live for cached files
     * @param storeType          Store type to be used
     * @param cacheFolder        local cache folder to be used on device
     */
    public FileStore(NetworkFileManager networkFileManager, ICacheManager cacheManager, Long ttl, StoreType storeType, String cacheFolder) {
        super(networkFileManager, cacheManager, ttl, storeType, cacheFolder);
    }


}
