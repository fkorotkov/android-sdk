/** 
 * Copyright (c) 2014, Kinvey, Inc. All rights reserved.
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
package com.kinvey.nativejava.store;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.json.MockJsonFactory;
import com.kinvey.java.AbstractClient;
import com.kinvey.java.CustomEndpoints;
import com.kinvey.java.Query;
import com.kinvey.java.UserDiscovery;
import com.kinvey.java.UserGroup;
import com.kinvey.java.auth.ClientUser;
import com.kinvey.java.cache.ICacheManager;
import com.kinvey.java.core.AbstractKinveyClientRequest;
import com.kinvey.java.core.KinveyClientRequestInitializer;
import com.kinvey.java.core.KinveyHeaders;
import com.kinvey.java.network.NetworkManager;
import com.kinvey.java.query.MongoQueryFilter;
import com.kinvey.nativejava.Client;

import junit.framework.TestCase;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JavaLibMockUnitTest extends TestCase {

    private MockTestClient mockClient;
        
    public MockTestClient getClient(){
    	if (mockClient == null){
    		mockClient = new MockTestClient.Builder(new MockHttpTransport(),new MockJsonFactory(), null, new MockKinveyClientRequestInitializer()).build();
    	}
    	return mockClient;
    }
    
    public MockTestClient getClient(HttpTransport transport){
    	return new MockTestClient.Builder(transport, new GsonFactory(), null, new MockKinveyClientRequestInitializer()).build();
    }



    public KinveyClientRequestInitializer getKinveyRequestInitializer(){

        return (KinveyClientRequestInitializer) mockClient.getKinveyRequestInitializer();
    }

    protected static class MockTestClient extends Client {

        private ConcurrentHashMap<String, NetworkManager> appDataInstanceCache;


        MockTestClient(HttpTransport transport, HttpRequestInitializer httpRequestInitializer,
                       String rootUrl, String servicePath, JsonObjectParser objectParser,
                       KinveyClientRequestInitializer kinveyRequestInitializer) {
            super(transport, null, "https://baas.kinvey.com/", "" , objectParser, kinveyRequestInitializer,null,null);
        }


        @Override
        public void performLockDown() {
            super.performLockDown();
        }


        @Override
        public com.kinvey.nativejava.UserDiscovery userDiscovery() {
            return super.userDiscovery();  //To change body of implemented methods use NetworkFileManager | Settings | NetworkFileManager Templates.
        }

        @Override
        public com.kinvey.nativejava.UserGroup userGroup() {
            return super.userGroup();  //To change body of implemented methods use NetworkFileManager | Settings | NetworkFileManager Templates.
        }

        public ClientUser getClientUser() {
            return super.getClientUser();
        }

        @Override
        public <I extends GenericJson, O> com.kinvey.nativejava.CustomEndpoints<I, O> customEndpoints(Class<O> myClass) {
            return super.customEndpoints(myClass);
        }

        @Override
        public ICacheManager getCacheManager() {
            return super.getCacheManager();
        }

        @Override
        public String getFileCacheFolder() {
            return super.getFileCacheFolder();
        }

        @Override
        protected ICacheManager getSyncCacheManager() {
            return super.getSyncCacheManager();
        }


        public static final class Builder extends AbstractClient.Builder {

            public Builder(HttpTransport transport, JsonFactory jsonFactory,
                           HttpRequestInitializer httpRequestInitializer,
                           KinveyClientRequestInitializer clientRequestInitializer) {
                super(transport, null , null);
                this.setJsonFactory(jsonFactory);
            }

            @Override
            public MockTestClient build() {
                return new MockTestClient(getTransport(), getHttpRequestInitializer(), getBaseUrl(), getServicePath(),
                        getObjectParser(), new MockKinveyClientRequestInitializer());
            }
        }
    }



    static class MockKinveyClientRequestInitializer extends KinveyClientRequestInitializer {

        boolean isCalled;

        MockKinveyClientRequestInitializer() {
            super("appkey", "appsecret", new KinveyHeaders());
        }

        @Override
        public void initialize(AbstractKinveyClientRequest<?> request) {
            isCalled = true;
        }
    }

    public static class MockQuery extends Query {

        public MockQuery(MockQueryFilter.MockBuilder builder) {
            super(null);
        }

        @Override
        public LinkedHashMap<String, Object> getQueryFilterMap() {
            LinkedHashMap<String,Object> filter = new LinkedHashMap<String,Object>();
            LinkedHashMap<String,Object> innerFilter = new LinkedHashMap<String, Object>();
            filter.put("city", "boston");
            innerFilter.put("$gt", 18);
            innerFilter.put("$lt", 21);
            filter.put("age", innerFilter);
            return filter;
        }

        @Override
        public String getQueryFilterJson(JsonFactory factory) {
            LinkedHashMap<String,Object> filter = new LinkedHashMap<String,Object>();
            LinkedHashMap<String,Object> innerFilter = new LinkedHashMap<String, Object>();
            filter.put("city","boston");
            innerFilter.put("$gt",18);
            innerFilter.put("$lt",21);
            filter.put("age",innerFilter);
            return filter.toString();
        }
    }

    public static class MockQueryFilter extends MongoQueryFilter {
        public static class MockBuilder extends MongoQueryFilterBuilder {
            public MockBuilder() {
                super();
            }

        }
    }

    public static class MockNetworkManager extends NetworkManager {
        /**
         * Constructor to instantiate the NetworkManager class.
         *
         * @param collectionName Name of the appData collection
         * @param myClass        Class Type to marshall data between.
         * @param client
         */
        protected MockNetworkManager(String collectionName, Class myClass, AbstractClient client) {
            super(collectionName, myClass, client);
        }
    }

}
