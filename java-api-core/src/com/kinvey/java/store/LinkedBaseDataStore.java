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

package com.kinvey.java.store;

import com.google.common.base.Preconditions;
import com.kinvey.java.AbstractClient;
import com.kinvey.java.LinkedResources.LinkedGenericJson;
import com.kinvey.java.MimeTypeFinder;
import com.kinvey.java.core.DownloaderProgressListener;
import com.kinvey.java.core.UploaderProgressListener;
import com.kinvey.java.network.LinkedNetworkManager;
import com.kinvey.java.network.NetworkManager;
import com.kinvey.java.store.requests.data.read.ReadSingleRequest;
import com.kinvey.java.store.requests.data.save.SaveRequest;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LinkedBaseDataStore<T extends LinkedGenericJson> extends BaseDataStore<T> {
    /**
     * Constructor for creating LinkedBaseDataStore for given collection that will be mapped to itemType class
     *
     * @param client     Kinvey client instance to work with
     * @param collection collection name
     * @param itemType   class that data should be mapped to
     * @param storeType  type of storage that client want to use
     */
    public LinkedBaseDataStore(AbstractClient client, String collection, Class<T> itemType, StoreType storeType, MimeTypeFinder finder) {
        super(client, collection, itemType, storeType,
                new LinkedNetworkManager<T>(collection, itemType, client, finder));
    }

    @Nonnull
    public T saveBlocking(@Nonnull T object, @Nullable UploaderProgressListener progressListener) throws IOException {
        Preconditions.checkNotNull(client, "client must not be null.");
        Preconditions.checkArgument(client.isInitialize(), "client must be initialized.");
        Preconditions.checkNotNull(object, "object must not be null.");
        return new SaveRequest<T>(cache, networkManager, this.storeType.writePolicy, object, client.getSyncManager(), progressListener).execute();
    }

    @Nonnull
    public T findBlocking(@Nonnull String id, @Nullable DownloaderProgressListener progressListener) throws IOException {
        Preconditions.checkNotNull(client, "client must not be null.");
        Preconditions.checkArgument(client.isInitialize(), "client must be initialized.");
        return new ReadSingleRequest<>(cache, id, this.storeType.readPolicy, networkManager, progressListener).execute();
    }

}
