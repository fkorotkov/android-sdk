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

package com.kinvey.java.sync;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.GenericJson;
import com.google.gson.Gson;
import com.kinvey.java.AbstractClient;
import com.kinvey.java.Query;
import com.kinvey.java.cache.ICache;
import com.kinvey.java.cache.ICacheManager;
import com.kinvey.java.core.AbstractKinveyJsonClientRequest;
import com.kinvey.java.network.NetworkManager;
import com.kinvey.java.query.MongoQueryFilter;
import com.kinvey.java.store.BaseDataStore;
import com.kinvey.java.store.StoreType;
import com.kinvey.java.sync.dto.SyncCollections;
import com.kinvey.java.sync.dto.SyncItem;
import com.kinvey.java.sync.dto.SyncRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Prots on 2/24/16.
 */
public class SyncManager {

    private static final String SYNC_ITEM_TABLE_NAME = "syncitems";
    private static final String COLLECTION = "collection";
    private static final String COLLECTION_NAME = "collectionName";
    private static final String SYNC = "sync";
    private static final String META_DOT_ID = "meta.id";
    private static final String META_ID = "meta._id";
    private static final String ID = "_id";
    private static final String UTF_8 = "UTF-8";
    private static final String ENTITY_ID = "entityID";
    private static final String SYNC_COLLECTIONS = "syncCollections";

    private ICacheManager cacheManager;

    public SyncManager(ICacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    public void removeEntity(String collectionName, String curEntityID) {
        Query query = new Query(new MongoQueryFilter.MongoQueryFilterBuilder());
        query.equals(COLLECTION_NAME, collectionName)
                .equals(META_ID, curEntityID);
        cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE).delete(query);
    }

    public List<String> getCollectionTables() {
        ICache<SyncCollections> collectionsCache  = cacheManager.getCache(SYNC_COLLECTIONS, SyncCollections.class, Long.MAX_VALUE);
        List<SyncCollections> collections = collectionsCache.get();
        List<String> ret = new ArrayList<String>();
        for (SyncCollections collection : collections){
            ret.add(collection.getCollectionName());
        }
        return ret;
    }

    /**
     * use {@link #popSingleItemQueue(String)}
     */
    @Deprecated
    public List<SyncRequest> popSingleQueue(String collectionName) {
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        Query q = new Query(new MongoQueryFilter.MongoQueryFilterBuilder())
                .equals(COLLECTION, collectionName);
        List<SyncRequest> requests = requestCache.get(q);

        //delete request from the queue

        if (requests.size() > 0) {
            List<String> ids = new ArrayList<String>();
            for (SyncRequest request: requests){
                if(request != null){
                    ids.add(request.get(ID).toString());
                }
            }
            requestCache.delete(ids);
        }
        return requests;
    }

    public List<SyncItem> popSingleItemQueue(String collectionName) {
        ICache<SyncItem> requestCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        Query q = new Query(new MongoQueryFilter.MongoQueryFilterBuilder())
                .equals(COLLECTION, collectionName);
        return requestCache.get().size() !=0 ? requestCache.get(q) : null;
    }

    /**
     * use {@link #deleteCachedItem(String)}
     */
    @Deprecated
    public int deleteCachedRequest(String id) {
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        return requestCache.delete(id);
    }

    public int deleteCachedItem(String id) {
        ICache<SyncItem> requestCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        return requestCache.delete(id);
    }

    public int deleteCachedItems(Query q) {
        ICache<SyncItem> requestCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        return requestCache.delete(q);
    }

    /**
     * use {@link #enqueueRequest(String, NetworkManager, SyncItem.HttpVerb, String)}
     */
    @Deprecated
    public void enqueueRequest(String collectionName, AbstractKinveyJsonClientRequest clientRequest) throws IOException {
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        requestCache.save(createSyncRequest(collectionName, clientRequest));
    }

    /**
     * use {@link #enqueueRequests(String, NetworkManager, SyncItem.HttpVerb, List)}
     */
    @Deprecated
    public <T extends GenericJson> void enqueueRequests(String collectionName, NetworkManager<T> networkManager,  List<T> ret) throws IOException {
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        List<SyncRequest> syncRequests = new ArrayList<>();
        for (T t : ret) {
            syncRequests.add(createSyncRequest(collectionName, networkManager.saveBlocking(t)));
        }
        requestCache.save(syncRequests);
    }

    /**
     * Used to save sync requests that were not completed
     * @param request Sync request to be saved
     */
    public void enqueueRequest(SyncRequest request) {
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        requestCache.save(request);
    }

    public <T extends GenericJson> void enqueueRequest(String collectionName, NetworkManager<T> networkManager, SyncItem.HttpVerb httpMethod, String id) throws IOException {
        ICache<SyncItem> requestCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        SyncItem syncItem = prepareSyncItemRequest(requestCache, collectionName, networkManager, httpMethod, id);
        if (syncItem != null) {
            requestCache.save(syncItem);
        }
    }

    public <T extends GenericJson> void enqueueDeleteRequests(String collectionName, NetworkManager<T> networkManager, List<T> ret) throws IOException {
        ICache<SyncItem> requestCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        List<SyncItem> syncRequests = new ArrayList<>();
        String syncItemId;
        SyncItem syncItem;
        for (T t : ret) {
            syncItemId = (String) t.get(ID);
            syncItem = prepareSyncItemRequest(requestCache, collectionName, networkManager, SyncRequest.HttpVerb.DELETE, syncItemId);
            if (syncItem != null) {
                syncRequests.add(syncItem);
            }
        }
        requestCache.save(syncRequests);
    }

    public <T extends GenericJson> void enqueueSaveRequests(String collectionName, NetworkManager<T> networkManager, List<T> ret) throws IOException {
        ICache<SyncItem> requestCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        List<SyncItem> syncRequests = new ArrayList<>();
        String syncItemId;
        SyncItem syncItem;
        for (T t : ret) {
            syncItemId = (String) t.get(ID);
            syncItem = prepareSyncItemRequest(requestCache, collectionName, networkManager, networkManager.isTempId(t) ? SyncRequest.HttpVerb.POST : SyncRequest.HttpVerb.PUT, syncItemId);
            if (syncItem != null) {
                syncRequests.add(syncItem);
            }
        }
        requestCache.save(syncRequests);
    }

    public <T extends GenericJson> void enqueueDeleteRequests(String collectionName, NetworkManager<T> networkManager, Iterable<String> ids) throws IOException {
        ICache<SyncItem> requestCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        List<SyncItem> syncRequests = new ArrayList<>();
        SyncItem syncItem;
        for (String syncItemId : ids) {
            syncItem = prepareSyncItemRequest(requestCache, collectionName, networkManager, SyncRequest.HttpVerb.DELETE, syncItemId);
            if (syncItem != null) {
                syncRequests.add(syncItem);
            }
        }
        requestCache.save(syncRequests);
    }

    private <T extends GenericJson> SyncItem prepareSyncItemRequest(ICache<SyncItem> requestCache,
                                                                    String collectionName,
                                                                    NetworkManager<T> networkManager,
                                                                    SyncItem.HttpVerb httpMethod,
                                                                    String syncItemId) throws IOException {
        Query entityQuery = AbstractClient.sharedInstance().query();
        entityQuery.equals(META_DOT_ID, syncItemId);
        List<SyncItem> itemsList = requestCache.get(entityQuery);
        if (itemsList.isEmpty()) {
            return createSyncItem(collectionName, httpMethod, networkManager, syncItemId);
        } else if (httpMethod == SyncRequest.HttpVerb.DELETE) {
            requestCache.delete(entityQuery);
            return createSyncItem(collectionName, httpMethod, networkManager, syncItemId);
        }
        return null;
    }

    private SyncItem createSyncItem(String collectionName, SyncItem.HttpVerb httpMethod, NetworkManager networkManager, String id) throws IOException {
        SyncRequest.SyncMetaData entityID = new SyncRequest.SyncMetaData();
        if (id != null) {
            entityID.id = id;
        }
        entityID.customerVersion = networkManager.getClientAppVersion();
        entityID.customheader = networkManager.getCustomRequestProperties() != null ?
                (String) networkManager.getCustomRequestProperties().get("X-Kinvey-Custom-Request-Properties") : null;
        return new SyncItem(httpMethod, entityID, collectionName);
    }

    public SyncRequest createSyncRequest(String collectionName, AbstractKinveyJsonClientRequest clientRequest) throws IOException {
        HttpRequest httpRequest = clientRequest.buildHttpRequest();
        SyncRequest.SyncMetaData entityID = new SyncRequest.SyncMetaData();

        if (httpRequest.getContent() != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            httpRequest.getContent().writeTo(os);
            entityID.data = os.toString(UTF_8);
        }

        if (clientRequest.getJsonContent() != null) {
            entityID.id = clientRequest.getJsonContent().get(ID).toString();
        }
        if (clientRequest.containsKey(ENTITY_ID)){
            entityID.id = clientRequest.get(ENTITY_ID).toString();
        }
        entityID.customerVersion = clientRequest.getCustomerAppVersion();
        entityID.customheader = clientRequest.getCustomRequestProperties();

        return new SyncRequest(
                SyncRequest.HttpVerb.valueOf(clientRequest.getRequestMethod().toUpperCase()),
                entityID, httpRequest.getUrl(),
                collectionName
        );
    }

    /**
     * This methods gets the count of sync operation to be performed
     * @param collectionName the name of the collection we want to get the info
     * @return the count of sync objects for given collection
     */
    public long getCount(String collectionName){
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        ICache<SyncItem> requestItemCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        Query q = new Query(new MongoQueryFilter.MongoQueryFilterBuilder())
                .equals(COLLECTION, collectionName);
        return requestCache.count(q) + requestItemCache.count(q);
    }

    /**
     * This method uses the AnsycAppData API to execute requests.  It contains an if/else block for the verb of the request
     * and then calls the appropriate appdata method.  As it uses the Async API, every request has a callback.
     * <p/>
     * Dependant on the verb and the result, this method will also update the local database.
     *
     * @param client kinvey client to execute request with
     * @param request Sync request to be executed
     */
    public GenericJson executeRequest(final AbstractClient client, SyncRequest request) throws IOException {

        client.setClientAppVersion(request.getEntityID().customerVersion);
        client.setCustomRequestProperties(new Gson().fromJson(request.getEntityID().customheader, GenericJson.class));

        GenericJson entity = null;
        GenericJson result = null;

        try {
            if (request.getEntityID().data != null) {
                entity = client.getJsonFactory().createJsonParser(request.getEntityID().data).parse(GenericJson.class);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BaseDataStore networkDataStore = BaseDataStore.collection(request.getCollectionName(), GenericJson.class, StoreType.NETWORK, client);

        if (request.getHttpVerb().equals(SyncRequest.HttpVerb.PUT) || request.getHttpVerb().equals((SyncRequest.HttpVerb.POST))) {

            if (entity != null){
                try {
                    if (request.getHttpVerb().equals(SyncRequest.HttpVerb.POST)) {
                        // Remvove the _id, since this is a create operation
                        entity.set("_id", null);
                    }
                    result = networkDataStore.save(entity);
                } catch (Exception e){
                    enqueueRequest(request);
                    throw e;
                }
            }
        } else if (request.getHttpVerb().equals(SyncRequest.HttpVerb.DELETE)){
            String curID = request.getEntityID().id;

            if (curID != null && curID.startsWith("{") && curID.endsWith("}")){
                //it's a query
                Query q = new Query().setQueryString(curID);
                try {
                    networkDataStore.delete(q);
                } catch(Exception e) {
                    enqueueRequest(request);
                    throw e;
                }


            } else if (curID == null && request.getUrl().contains("?query=")) {

                String url = request.getUrl();
                int index = url.indexOf("?query=") + 7;
                String qString = url.substring(index);
                String decodedQuery = null;
                try {
                    decodedQuery = URLDecoder.decode(qString, UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (decodedQuery == null) {
                    return result;
                }
                Query q = new Query().setQueryString(decodedQuery);
                try {
                    networkDataStore.delete(q);
                } catch(Exception e) {
                    enqueueRequest(request);
                    throw e;
                }

            } else {
                    //it's a single ID
                    try {
                        networkDataStore.delete(request.getEntityID().id);
                    } catch(Exception e) {
                        //TODO: need to check the errors
                        //enqueueRequest(request);
                        throw e;
                    }
            }
        }

        return result;
    }

    public int clear(String collectionName) {
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        ICache<SyncItem> requestItemCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        Query q = new Query(new MongoQueryFilter.MongoQueryFilterBuilder())
                .equals(COLLECTION, collectionName);
        return requestCache.delete(q) + requestItemCache.delete(q);
    }

    public int clear(String collectionName, Query query) {
        ICache<SyncRequest> requestCache = cacheManager.getCache(SYNC, SyncRequest.class, Long.MAX_VALUE);
        ICache<SyncItem> requestItemCache = cacheManager.getCache(SYNC_ITEM_TABLE_NAME, SyncItem.class, Long.MAX_VALUE);
        query = query.equals(COLLECTION, collectionName);
        return requestCache.delete(query) + requestItemCache.delete(query);
    }

    public ICacheManager getCacheManager() {
        return cacheManager;
    }
}
