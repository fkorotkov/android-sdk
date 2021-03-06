package com.kinvey.androidTest.store.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.kinvey.android.AndroidCredentialStoreException;
import com.kinvey.android.Client;
import com.kinvey.android.store.DataStore;
import com.kinvey.androidTest.TestManager;
import com.kinvey.androidTest.callback.CustomKinveyClientCallback;
import com.kinvey.androidTest.callback.DefaultKinveyAggregateCallback;
import com.kinvey.androidTest.model.Person;
import com.kinvey.java.Query;
import com.kinvey.java.model.AggregateType;
import com.kinvey.java.store.StoreType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import static com.kinvey.androidTest.TestManager.PASSWORD;
import static com.kinvey.androidTest.TestManager.TEST_USERNAME;
import static com.kinvey.androidTest.TestManager.USERNAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by yuliya on 10/06/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AggregationTest {

    private Client client;

    @Before
    public void setUp() throws InterruptedException, IOException {
        Context mMockContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        client = new Client.Builder(mMockContext).build();
    }

    @Test
    public void testCountLocally() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);

        Person person = new Person(TEST_USERNAME);
        person.setHeight(170);
        Person person2 = new Person(TEST_USERNAME);
        person2.setHeight(170);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.COUNT, fields, null, query, null);
        assertNotNull(callback);
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 2);
    }

    @Test
    public void testCountNetwork() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.NETWORK, client);

        testManager.cleanBackendDataStore(store);

        Person person = new Person(TEST_USERNAME);
        person.setHeight(170);
        Person person2 = new Person(TEST_USERNAME);
        person2.setHeight(170);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.COUNT, fields, null, query, null);
        assertNotNull(callback);
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 2);
    }

    @Test
    public void testMinLocally() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);

        testManager.cleanBackendDataStore(store);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.MIN, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNull(callback.getError());
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 1);
    }

    @Test
    public void testMinNetwork() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.NETWORK, client);

        testManager.cleanBackendDataStore(store);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.MIN, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 1);
    }

    @Test
    public void testCountAuto() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.AUTO, client);

        testManager.cleanBackendDataStore(store);

        Person person = new Person(TEST_USERNAME);
        person.setHeight(170);
        Person person2 = new Person(TEST_USERNAME);
        person2.setHeight(170);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.COUNT, fields, null, query, null);
        assertNotNull(callback);
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 2);
    }

    @Test
    public void testMaxLocally() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.MAX, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNull(callback.getError());
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 2);
    }

    @Test
    public void testMaxNetwork() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.NETWORK, client);

        testManager.cleanBackendDataStore(store);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.MAX, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNull(callback.getError());
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 2);
    }

    @Test
    public void testAverageLocally() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);
        Person person3 = new Person(TEST_USERNAME);
        person3.setCarNumber(3);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));
        saveCallback = testManager.saveCustom(store, person3);
        assertNotNull(saveCallback.getResult().getUsername().equals(person3.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.AVERAGE, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNull(callback.getError());
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 2);
    }

    @Test
    public void testAndroidCredentialStoreExceptionMessage() {
        AndroidCredentialStoreException androidCredentialStoreException =  new AndroidCredentialStoreException("Credential store corrupted and was rebuilt");
        assertEquals(androidCredentialStoreException.getMessage(), "Credential store corrupted and was rebuilt");
    }

    @Test
    public void testAndroidCredentialStoreExceptionConstructor() {
        AndroidCredentialStoreException androidCredentialStoreException =  new AndroidCredentialStoreException();
        assertNotNull(androidCredentialStoreException);
    }

    @Test
    public void testAverageNetwork() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.NETWORK, client);

        testManager.cleanBackendDataStore(store);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);
        Person person3 = new Person(TEST_USERNAME);
        person3.setCarNumber(3);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));
        saveCallback = testManager.saveCustom(store, person3);
        assertNotNull(saveCallback.getResult().getUsername().equals(person3.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.AVERAGE, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNull(callback.getError());
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 2);
    }


    @Test
    public void testSumLocally() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);
        Person person3 = new Person(TEST_USERNAME);
        person3.setCarNumber(3);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));
        saveCallback = testManager.saveCustom(store, person3);
        assertNotNull(saveCallback.getResult().getUsername().equals(person3.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.SUM, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNull(callback.getError());
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 6);
    }

    @Test
    public void testSumNetwork() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.NETWORK, client);

        testManager.cleanBackendDataStore(store);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);
        Person person3 = new Person(TEST_USERNAME);
        person3.setCarNumber(3);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));
        saveCallback = testManager.saveCustom(store, person3);
        assertNotNull(saveCallback.getResult().getUsername().equals(person3.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.SUM, fields, "carNumber", query, null);
        assertNotNull(callback);
        assertNull(callback.getError());
        assertNotNull(callback.getResult().getResultsFor("username", TEST_USERNAME));
        assertTrue(callback.getResult().getResultsFor("username", TEST_USERNAME).get(0).intValue() == 6);
    }

    @Test
    public void testSumForStringFieldsError() throws InterruptedException, IOException {
        TestManager<Person> testManager = new TestManager<Person>();
        testManager.login(USERNAME, PASSWORD, client);
        DataStore<Person> store = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);

        Person person = new Person(TEST_USERNAME);
        person.setCarNumber(1);
        Person person2 = new Person(TEST_USERNAME);
        person2.setCarNumber(2);
        Person person3 = new Person(TEST_USERNAME);
        person3.setCarNumber(3);

        CustomKinveyClientCallback<Person> saveCallback = testManager.saveCustom(store, person);
        assertNotNull(saveCallback.getResult().getUsername().equals(person.getUsername()));
        saveCallback = testManager.saveCustom(store, person2);
        assertNotNull(saveCallback.getResult().getUsername().equals(person2.getUsername()));
        saveCallback = testManager.saveCustom(store, person3);
        assertNotNull(saveCallback.getResult().getUsername().equals(person3.getUsername()));

        Query query = client.query();
        query = query.notEqual("age", "100200300");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("username");
        DefaultKinveyAggregateCallback callback = testManager.calculation(store, AggregateType.SUM, fields, "username", query, null);
        assertNotNull(callback);
        assertNotNull(callback.getError());
        assertEquals(callback.getError().getMessage(),"Field 'username': type mismatch - int, float or double expected.");
    }

    @After
    public void tearDown() {
        client.performLockDown();
        if (client.getKinveyHandlerThread() != null) {
            try {
                client.stopKinveyHandlerThread();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

}
