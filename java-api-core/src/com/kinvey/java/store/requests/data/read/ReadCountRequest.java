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

package com.kinvey.java.store.requests.data.read;

import com.google.api.client.json.GenericJson;
import com.kinvey.java.Query;
import com.kinvey.java.cache.ICache;
import com.kinvey.java.network.NetworkManager;
import com.kinvey.java.store.ReadPolicy;
import com.kinvey.java.sync.SyncManager;

import java.io.IOException;

public class ReadCountRequest<T extends GenericJson> extends AbstractReadCountRequest<T> {

    private final Query query;

    public ReadCountRequest(ICache<T> cache, NetworkManager<T> networkManager, ReadPolicy readPolicy,
                              Query query, SyncManager syncManager) {
        super(cache, readPolicy, networkManager, syncManager);
        this.query = query;
    }

    @Override
    protected Integer countCached() {
        return ((Long) cache.count(query)).intValue();
    }

    @Override
    protected NetworkManager.GetCount countNetwork() throws IOException {
        return networkManager.getCountBlocking();
    }
}
