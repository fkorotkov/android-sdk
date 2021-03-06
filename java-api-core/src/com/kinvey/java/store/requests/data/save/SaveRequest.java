/*
 *  Copyright (c) 2016, Kinvey, Inc. All rights reserved.
 *
 * This software is licensed to you under the Kinvey terms of service located at
 * http://www.kinvey.com/terms-of-use. By downloading, accessing and/or using this
 * software, you hereby accept such terms of service  (and any agreement referenced
 * therein) and agree that you have read, understand and agree to be bound by such
 * terms of service and are of legal age to agree to such terms with Kinvey.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 *
 */

package com.kinvey.java.store.requests.data.save;

import com.google.api.client.json.GenericJson;
import com.kinvey.java.Constants;
import com.kinvey.java.Logger;
import com.kinvey.java.cache.ICache;
import com.kinvey.java.network.NetworkManager;
import com.kinvey.java.store.WritePolicy;
import com.kinvey.java.store.requests.data.IRequest;
import com.kinvey.java.store.requests.data.PushRequest;
import com.kinvey.java.sync.SyncManager;
import com.kinvey.java.sync.dto.SyncRequest;

import java.io.IOException;

/**
 * Created by Prots on 2/5/16.
 */
public class SaveRequest<T extends GenericJson> implements IRequest<T> {
    private final ICache<T> cache;
    private final T object;
    private final WritePolicy writePolicy;
    private SyncManager syncManager;
    private NetworkManager<T> networkManager;

    public SaveRequest(ICache<T> cache, NetworkManager<T> networkManager,
                       WritePolicy writePolicy, T object,
                       SyncManager syncManager) {
        this.networkManager = networkManager;
        this.cache = cache;
        this.object = object;
        this.writePolicy = writePolicy;
        this.syncManager = syncManager;
    }

    @Override
    public T execute() throws IOException {
        T ret = null;
        switch (writePolicy){
            case FORCE_LOCAL:
                ret = cache.save(object);
                syncManager.enqueueRequest(networkManager.getCollectionName(),
                        networkManager, networkManager.isTempId(ret) ? SyncRequest.HttpVerb.POST : SyncRequest.HttpVerb.PUT, (String)object.get(Constants._ID));
                break;
            case LOCAL_THEN_NETWORK:
                PushRequest<T> pushRequest = new PushRequest<T>(networkManager.getCollectionName(), cache, networkManager,
                        networkManager.getClient());
                try {
                    pushRequest.execute();
                } catch (Throwable t){
                    // silent fall, will be synced next time
                }

                // If object does not have an _id, then it is being created locally. The cache may
                // provide an _id in this case, but before it is saved to the network, this temporary
                // _id should be removed prior to saving to the backend. This way, the backend
                // will generate a permanent _id that will be used by the cache. Once we get the
                // result from the backend with the permanent _id, the record in the cache with the
                // temporary _id should be removed, and the new record should be saved.
                ret = cache.save(object);
                String id = ret.get(Constants._ID).toString();
                boolean bRealmGeneratedId = networkManager.isTempId(ret);
                if (bRealmGeneratedId) {
                    ret.set(Constants._ID, null);
                }
                try{
                    ret = networkManager.saveBlocking(object).execute();
                    if (bRealmGeneratedId) {
                        // The result from the network has the entity with its permanent ID. Need
                        // to remove the entity from the local cache with the temporary ID.
                        cache.delete(id);
                    }

                } catch (IOException e) {
                    syncManager.enqueueRequest(networkManager.getCollectionName(),
                            networkManager, bRealmGeneratedId ? SyncRequest.HttpVerb.POST : SyncRequest.HttpVerb.PUT, id);
                    throw e;
                }
                cache.save(ret);
                break;
            case FORCE_NETWORK:
                Logger.INFO("Start saving entity");
                ret = networkManager.saveBlocking(object).execute();
                Logger.INFO("Finish saving entity");
                break;
        }
        return ret;
    }

    @Override
    public void cancel() {
    }
}
