package com.kinvey.nativejava.store;

import com.kinvey.java.store.StoreType;
import com.kinvey.nativejava.Client;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;

public class FileStoreTest {

    //    public static final String appKey = "kid_WJxJbK9kC";
//    public static final String appSecret = "f16df5c4be864d34aff7d1cc962f20b9";
    private static final String appKey = "kid_H1lH5Dsw";
    private static final String appSecret = "cc5c72d261d8473590a0fa01024fb313";
    private StoreType storeType;

    private FileStore fileStore;
    private Client client;

    @Before
    public void setUp() throws Exception {
        storeType = StoreType.NETWORK;
        initializeClient();
        if (!client.isUserLoggedIn()) {
            login();
        }
        initializeDataStore();
    }

    private void initializeClient() {
        client = new Client.Builder(appKey, appSecret).build();
    }

    private void initializeDataStore() {
        fileStore = client.getFileStore(storeType);
    }

    private void login() {
        try {
            UserStore.login(client);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue("User was not logged", false);
        }
    }

    private void createFile() {

    }


    @Test
    public void upload() throws Exception {

    }

    @Test
    public void upload1() throws Exception {

    }

    @Test
    public void upload2() throws Exception {

    }

    @Test
    public void upload3() throws Exception {

    }

    @Test
    public void remove() throws Exception {

    }

    @Test
    public void find() throws Exception {

    }

    @Test
    public void find1() throws Exception {

    }

    @Test
    public void refresh() throws Exception {

    }

    @Test
    public void download() throws Exception {

    }

    @Test
    public void setStoreType() throws Exception {

    }

    @Test
    public void getCacheFolder() throws Exception {

    }

}