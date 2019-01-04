package com.kinvey.androidTest.store.data;

import com.kinvey.android.Client;
import com.kinvey.android.store.DataStore;
import com.kinvey.androidTest.TestManager;
import com.kinvey.androidTest.model.Person;
import com.kinvey.java.store.StoreType;

public class BaseTest {

    protected Client client;

    public void setUp() throws InterruptedException {
        cleanBackendData();
    }

    public void tearDown() throws InterruptedException {
        cleanBackendData();
    }

    private void cleanBackendData() throws InterruptedException {
        TestManager<Person> testManager = new TestManager<>();
        testManager.login(TestManager.USERNAME, TestManager.PASSWORD, client);
        if (client != null && client.isInitialize()) {
            DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.NETWORK, client);
            testManager.cleanBackendDataStore(store);

            DataStore<Person> store2 = DataStore.collection(Person.DELTA_SET_COLLECTION, Person.class, StoreType.NETWORK, client);
            testManager.cleanBackendDataStore(store2);

            DataStore<Person> store3 = DataStore.collection(Person.DELTA_SET_OFF_COLLECTION, Person.class, StoreType.NETWORK, client);
            testManager.cleanBackendDataStore(store3);
        }
    }

}
