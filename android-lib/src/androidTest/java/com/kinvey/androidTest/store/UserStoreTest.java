package com.kinvey.androidTest.store;


import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyPurgeCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.android.sync.KinveyPullCallback;
import com.kinvey.android.sync.KinveyPullResponse;
import com.kinvey.android.sync.KinveyPushCallback;
import com.kinvey.android.sync.KinveyPushResponse;
import com.kinvey.android.sync.KinveySyncCallback;
import com.kinvey.androidTest.model.Person;
import com.kinvey.java.Query;
import com.kinvey.java.cache.ICache;
import com.kinvey.java.cache.ICacheManager;
import com.kinvey.java.cache.KinveyCachedClientCallback;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.dto.User;
import com.kinvey.java.store.StoreType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UserStoreTest {

    private Client client;


    @Before
    public void setUp() throws InterruptedException, IOException {
        Context mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        client = new Client.Builder(mMockContext).build();
        if (client.isUserLoggedIn()) {
            UserStore.logout(client);
        }
    }

    @After
    public void tearDown() {
        if (client.isUserLoggedIn()) {
            UserStore.logout(client);
        }
    }

    private static class DefaultKinveyLoginCallback implements KinveyClientCallback<User> {

        private CountDownLatch latch;
        User result;
        Throwable error;

        DefaultKinveyLoginCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(User result) {
            this.result = result;
            finish();
        }

        @Override
        public void onFailure(Throwable error) {
            this.error = error;
            finish();
        }

        void finish() {
            latch.countDown();
        }
    }

    /*--------------------------------------------Tests-------------------------------------------*/

    @Test
    public void testLoginAsync() throws InterruptedException {
        DefaultKinveyLoginCallback userCallback = login(client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
        UserStore.logout(client);
    }

    private DefaultKinveyLoginCallback login(final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyLoginCallback callback = new DefaultKinveyLoginCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    UserStore.login(client, callback);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Looper.loop();
            }
        }).start();
        latch.await();
        return callback;
    }

    private DefaultKinveyLoginCallback login(final String user, final String password, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyLoginCallback callback = new DefaultKinveyLoginCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    UserStore.login(user, password, client, callback);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Looper.loop();
            }
        }).start();
        latch.await();
        return callback;
    }

    @Test
    public void testSharedClientLoginAsync() throws InterruptedException {
        DefaultKinveyLoginCallback userCallback = login(Client.sharedInstance());
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
        UserStore.logout(client);
    }


    @Test
    public void testLoginAsyncBad() throws InterruptedException {
        Context mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        Client fakeClient = new Client.Builder("app_key_fake", "app_secret_fake", mMockContext).build();

        DefaultKinveyLoginCallback userCallback = login(fakeClient);
        assertNotNull(userCallback.error);

        userCallback = login("test", "test", fakeClient);
        assertNotNull(userCallback.error);
    }


    @Test
    public void testLoginUserPassAsync() throws InterruptedException {
        DefaultKinveyLoginCallback userCallback = login("test", "test", client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
        UserStore.logout(client);
    }


    @Test
    public void testLoginUserPassAsyncBad() throws InterruptedException {
        DefaultKinveyLoginCallback userCallback = login("test", "wrongPassword", client);
        assertNotNull(userCallback.error);
        assertFalse(client.isUserLoggedIn());
    }

    /*------------------------------------------------------------------------------------------*/

}

