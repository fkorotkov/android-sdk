package com.kinvey.androidTest.store.data.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.kinvey.android.Client;
import com.kinvey.android.network.AndroidNetworkManager;
import com.kinvey.androidTest.model.Person;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AndroidNetworkManagerTest {

    private Client client;

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

    @Test
    public void testIsOnline() {
        Context mMockContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        client = new Client.Builder(mMockContext).build();
        AndroidNetworkManager androidNetworkManager = new AndroidNetworkManager<>(Person.COLLECTION, Person.class, client);
        assertNotNull(androidNetworkManager);
        assertTrue(androidNetworkManager.isOnline());
    }

}
