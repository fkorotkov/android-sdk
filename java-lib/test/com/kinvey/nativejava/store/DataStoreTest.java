package com.kinvey.nativejava.store;

import com.kinvey.java.Logger;
import com.kinvey.java.dto.User;
import com.kinvey.java.store.StoreType;
import com.kinvey.nativejava.Client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class DataStoreTest {

    //    public static final String appKey = "kid_WJxJbK9kC";
//    public static final String appSecret = "f16df5c4be864d34aff7d1cc962f20b9";
    private static final String appKey = "kid_H1lH5Dsw";
    private static final String appSecret = "cc5c72d261d8473590a0fa01024fb313";
    private StoreType storeType;

    private DataStore<TestEntity> dataStore;
    private Client client;
    private User user;

    @Before
    public void setUp() throws Exception {
        storeType = StoreType.CACHE;
        initializeClient();
        initializeDataStore();
        if (!client.isUserLoggedIn()) {
            login();
        }
    }

    @After
    public void tearDown() throws Exception {

    }


    private void initializeClient() {
        client = new Client.Builder(appKey, appSecret).build();
    }

    private void initializeDataStore() {
        dataStore = DataStore.collection(TestEntity.COLLECTION, TestEntity.class, storeType, client);
    }

    private void login() {
        try {
            user = UserStore.login(client);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue("User was not logged", false);
        }
    }

    private TestEntity createEntity() {
        TestEntity entity = new TestEntity();
        entity.setId("test_id_" + System.currentTimeMillis());
        return entity;
    }


    @Test
    public void testFind() throws Exception {
        ArrayList<String> ent = new ArrayList<>();
        ent.add(dataStore.save(createEntity()).getId());
        ent.add(dataStore.save(createEntity()).getId());
        ent.add(dataStore.save(createEntity()).getId());
        List<TestEntity> entities = dataStore.find(ent);
        for (TestEntity e : entities) {
            assertTrue(ent.contains(e.getId()));
        }
    }

    @Test
    public void testSave() throws Exception {
        TestEntity savedEntity = dataStore.save(createEntity());
        assertEquals(savedEntity.getId(), dataStore.find(savedEntity.getId()).getId());
        dataStore.delete(savedEntity.getId());
    }

    @Test
    public void testDelete() throws Exception {
        TestEntity savedEntity = dataStore.save(createEntity());
        assertEquals(savedEntity, dataStore.find(savedEntity.getId()));
        assertEquals(Integer.valueOf(1), dataStore.delete(savedEntity.getId()));
    }

    @Test
    public void testPushBlocking() throws IOException {
        try {
            dataStore.pushBlocking();
        } catch (IllegalArgumentException e) {
            if (storeType == StoreType.NETWORK) {
                assertEquals(e.getMessage(), "InvalidDataStoreType");
            } else {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPullBlocking() throws IOException {
        try {
            dataStore.pullBlocking(null);
        } catch (IllegalArgumentException e) {
            if (storeType == StoreType.NETWORK) {
                assertEquals(e.getMessage(), "InvalidDataStoreType");
            } else {
                e.printStackTrace();
            }
        }
        Logger.DEBUG("test");
    }

    @Test
    public void testSyncBlocking() throws IOException {
        try {
            dataStore.syncBlocking(null);
        } catch (IllegalArgumentException e) {
            if (storeType == StoreType.NETWORK) {
                assertEquals(e.getMessage(), "InvalidDataStoreType");
            } else {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPurge() throws IOException {
        try {
            dataStore.purge();
        } catch (IllegalArgumentException e) {
            if (storeType == StoreType.NETWORK) {
                assertEquals(e.getMessage(), "InvalidDataStoreType");
            } else {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetCollectionName() throws Exception {
        assertEquals(dataStore.getCollectionName(), TestEntity.COLLECTION);
    }

}