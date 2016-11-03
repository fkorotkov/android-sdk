package com.kinvey.nativejava.store;

import com.google.api.client.json.GenericJson;
import com.google.common.base.Preconditions;
import com.kinvey.java.AbstractClient;
import com.kinvey.java.store.BaseDataStore;
import com.kinvey.java.store.StoreType;

/**
 * Created on 03.11.2016.
 */

public class DataStore <T extends GenericJson> extends BaseDataStore<T> {

    private DataStore(String collectionName, Class<T> myClass, AbstractClient client, StoreType storeType) {
        super(client, collectionName, myClass, storeType);
    }

    public static <T extends GenericJson> DataStore<T> collection(String collectionName, Class<T> myClass, StoreType storeType, AbstractClient client) {
        Preconditions.checkNotNull(collectionName, "collectionName cannot be null.");
        Preconditions.checkNotNull(storeType, "storeType cannot be null.");
        Preconditions.checkArgument(storeType == StoreType.NETWORK, "storeType cannot be CACHE or SYNC.");
        Preconditions.checkArgument(client.isInitialize(), "client must be initialized.");
        return new DataStore<T>(collectionName, myClass, client, storeType);
    }

}
