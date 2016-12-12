package com.kinvey.androidTest.store;


import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyMICCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.callback.KinveyUserManagementCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.androidTest.model.Person;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.dto.User;
import com.kinvey.java.store.StoreType;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

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
    private final String username = "test";
    private final String password = "test";
    private final String appKey = "kid_H1lH5Dsw";
    private final String appSecret = "cc5c72d261d8473590a0fa01024fb313";

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

    private static class DefaultKinveyClientCallback implements KinveyClientCallback<User> {

        private CountDownLatch latch;
        User result;
        Throwable error;

        DefaultKinveyClientCallback(CountDownLatch latch) {
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

    private static class DefaultKinveyMICCallback implements KinveyMICCallback<User> {

        private CountDownLatch latch;
        User result;
        Throwable error;
        String myURLToRender;

        DefaultKinveyMICCallback(CountDownLatch latch) {
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

        @Override
        public void onReadyToRender(String myURLToRender) {
            this.myURLToRender = myURLToRender;
            finish();
        }
    }

    private static class DefaultKinveyUserCallback implements KinveyUserCallback<User> {

        private CountDownLatch latch;
        User result;
        Throwable error;

        DefaultKinveyUserCallback(CountDownLatch latch) {
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

    private static class DefaultPersonKinveyClientCallback implements KinveyClientCallback<Person> {

        private CountDownLatch latch;
        Person result;
        Throwable error;

        DefaultPersonKinveyClientCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(Person result) {
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

    private static class DefaultKinveyUserManagementCallback implements KinveyUserManagementCallback {

        private CountDownLatch latch;
        boolean result;
        Throwable error;

        DefaultKinveyUserManagementCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSuccess(Void v) {
            result = true;
            finish();
        }

        @Override
        public void onFailure(Throwable error) {
            result = false;
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
        DefaultKinveyClientCallback userCallback = login(client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
        UserStore.logout(client);
    }

    private DefaultKinveyClientCallback login(final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
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

    private DefaultKinveyClientCallback login(final String user, final String password, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
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
        DefaultKinveyClientCallback userCallback = login(Client.sharedInstance());
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
        UserStore.logout(client);
    }


    @Test
    public void testLoginAsyncBad() throws InterruptedException {
        Context mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        Client fakeClient = new Client.Builder("app_key_fake", "app_secret_fake", mMockContext).build();

        DefaultKinveyClientCallback userCallback = login(fakeClient);
        assertNotNull(userCallback.error);

        userCallback = login(username, password, fakeClient);
        assertNotNull(userCallback.error);
    }


    @Test
    public void testLoginUserPassAsync() throws InterruptedException {
        DefaultKinveyClientCallback userCallback = login(username, password, client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
        UserStore.logout(client);
    }


    @Test
    public void testLoginUserPassAsyncBad() throws InterruptedException {
        DefaultKinveyClientCallback userCallback = login(username, "wrongPassword", client);
        assertNotNull(userCallback.error);
        assertFalse(client.isUserLoggedIn());
    }


    @Test
    @Ignore // need to add facebookAccessToken
    //EAAKY5ePO2X0BAK5RVglpQEC3lvRh7onxIzWdJdZAme61n9nLKh3w8RK59AtBM5tLDRZAqpK3QsLwHIUECqChk7eNVTjxeqBqsgQA3uXyghe4zlLfPZAulXdJz5MMC5wZCGxHJZAueNVmEoMJ0DTChQs5rsZCqh9rl8BcN4SzMgSwZDZD
    public void testLoginFacebookAsync() throws InterruptedException {
        String facebookAccessToken = "";
        DefaultKinveyClientCallback userCallback = loginFacebook(facebookAccessToken, client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
    }


    @Test
    public void testLoginFacebookAsyncBad() throws InterruptedException {
        String facebookAccessToken = "wrong_access_token";
        DefaultKinveyClientCallback userCallback = loginFacebook(facebookAccessToken, client);
        assertNotNull(userCallback.error);
        assertFalse(client.isUserLoggedIn());
    }

    private DefaultKinveyClientCallback loginFacebook(final String accessToken, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    UserStore.loginFacebook(accessToken, client, callback);
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
    @Ignore // need to add googleAccessToken ya29.Ci-vA9oNKCrCCfKWPldBnj-5c1uL85HomkpcfqXk6dk_OaHCabOlFYp7XXWw_zwrzQ
    public void testLoginGoogleAsync() throws InterruptedException {
        String googleAccessToken = "";
        DefaultKinveyClientCallback userCallback = loginGoogle(googleAccessToken, client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
    }


    @Test
    public void testLoginGoogleAsyncBad() throws InterruptedException {
        String googleAccessToken = "wrong_access_token";
        DefaultKinveyClientCallback userCallback = loginGoogle(googleAccessToken, client);
        assertNotNull(userCallback.error);
        assertFalse(client.isUserLoggedIn());
    }

    private DefaultKinveyClientCallback loginGoogle(final String accessToken, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    UserStore.loginGoogle(accessToken, client, callback);
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
    @Ignore // need to add accessToken,  accessSecret, consumerKey, consumerSecret
    public void testLoginTwitterAsync() throws InterruptedException {
        String accessToken = "";
        String accessSecret = "";
        String consumerKey = "";
        String consumerSecret = "";
        DefaultKinveyClientCallback userCallback = loginTwitter(accessToken, accessSecret, consumerKey, consumerSecret, client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
    }


    @Test
    public void testLoginTwitterAsyncBad() throws InterruptedException {
        String accessToken = "wrongAccessToken";
        String accessSecret = "wrongAccessSecret";
        String consumerKey = "wrongConsumerKey";
        String consumerSecret = "wrongConsumerSecret";
        DefaultKinveyClientCallback userCallback = loginTwitter(accessToken, accessSecret, consumerKey, consumerSecret, client);
        assertNotNull(userCallback.error);
        assertFalse(client.isUserLoggedIn());
    }

    private DefaultKinveyClientCallback loginTwitter(final String accessToken, final String accessSecret, final String consumerKey, final String consumerSecret, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    UserStore.loginTwitter(accessToken, accessSecret, consumerKey, consumerSecret, client, callback);
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

    // TODO: 09.12.2016 should be checked
    @Test
    @Ignore // need to add accessToken,  accessSecret, consumerKey, consumerSecret
    public void testLoginLinkedInAsync() throws InterruptedException {
        String accessToken = "AQXu60okmBXrQkBm5BOpBCBBpCYc3y9uKWHtF559A1j4ttwjf5bXNeq0nVOHtgPomuw9Wn661BYbZal-3IReW0zc-Ed8NvP0FNdOTQVt9c8qz9EL5sezCYKd_I2VPEEMSC-YOyvhi-7WsttjaPnU_9H_kCnfVJuU7Fyt8Ph1XTw66xZeu2U";
        String accessSecret = "ExAZxYxvo42UfOCN";
        String consumerKey = "86z99b0orhyt7s";
        String consumerSecret = "ExAZxYxvo42UfOCN";
        DefaultKinveyClientCallback userCallback = loginLinkedIn(accessToken, consumerSecret, consumerKey, consumerSecret, client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
    }


    @Test
    public void testLoginLinkedInAsyncBad() throws InterruptedException {
        String accessToken = "wrongAccessToken";
        String accessSecret = "wrongAccessSecret";
        String consumerKey = "wrongConsumerKey";
        String consumerSecret = "wrongConsumerSecret";
        DefaultKinveyClientCallback userCallback = loginLinkedIn(accessToken, accessSecret, consumerKey, consumerSecret, client);
        assertNotNull(userCallback.error);
        assertFalse(client.isUserLoggedIn());
    }

    private DefaultKinveyClientCallback loginLinkedIn(final String accessToken, final String accessSecret, final String consumerKey, final String consumerSecret, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    UserStore.loginLinkedIn(accessToken, accessSecret, consumerKey, consumerSecret, client, callback);
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

    // TODO: 09.12.2016 should be checked
    @Test
    @Ignore // need to add access,  reauth, clientID, ID
    public void testLoginSalesforceAsync() throws InterruptedException {
        String access = "";
        String reauth = "";
        String clientID = "";
        String ID = "";
        DefaultKinveyClientCallback userCallback = loginSalesforce(access, reauth, clientID, ID, client);
        assertNotNull(userCallback.result);
        assertTrue(client.isUserLoggedIn());
    }

    @Test
    public void testLoginSalesforceAsyncBad() throws InterruptedException {
        String access = "wrongAccess";
        String reauth = "wrongReauth";
        String clientID = "wrongClientID";
        String ID = "wrongID";
        DefaultKinveyClientCallback userCallback = loginSalesforce(access, reauth, clientID, ID, client);
        assertNotNull(userCallback.error);
        assertFalse(client.isUserLoggedIn());
    }

    private DefaultKinveyClientCallback loginSalesforce(final String access, final String reauth, final String clientID, final String ID, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    UserStore.loginSalesForce(access, reauth, clientID, ID, client, callback);
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
    public void testMIC_LoginWithAuthorizationCodeLoginPage() throws InterruptedException {
        String redirectURI = "http://test.redirect";
        DefaultKinveyMICCallback userCallback = loginWithAuthorizationCodeLoginPage(redirectURI, client);
        assertNotNull(userCallback.myURLToRender);
        assertTrue(!userCallback.myURLToRender.isEmpty());
        assertTrue(userCallback.myURLToRender.startsWith(client.getMICHostName() + "oauth/auth?client_id"));
    }

    private DefaultKinveyMICCallback loginWithAuthorizationCodeLoginPage(final String redirectUrl, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyMICCallback callback = new DefaultKinveyMICCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                UserStore.loginWithAuthorizationCodeLoginPage(client, redirectUrl, callback);
                Looper.loop();
            }
        }).start();
        latch.await();
        return callback;
    }


    @Test
    public void testMIC_LoginWithAuthorizationCodeAPI() throws InterruptedException {
        String username = "test";
        String password = "test";
        String redirectURI = "kinveyAuthDemo://";
        String saml_app_key = "kid_H1lH5Dsw";
        String saml_app_secret = "cc5c72d261d8473590a0fa01024fb313";
        Context mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        Client.Builder localBuilder = new Client.Builder(saml_app_key, saml_app_secret, mMockContext);
        Client localClient = localBuilder.build();
        localClient.setMICApiVersion("v2");

        DefaultKinveyUserCallback userCallback = LoginWithAuthorizationCodeAPIAsync(username, password, redirectURI, client);
        assertNotNull(userCallback.result);
        UserStore.logout(client);
    }

    private DefaultKinveyUserCallback LoginWithAuthorizationCodeAPIAsync(final String username, final String password, final String redirectUrl, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyUserCallback callback = new DefaultKinveyUserCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                UserStore.loginWithAuthorizationCodeAPI(client, username, password, redirectUrl, callback);
                Looper.loop();
            }
        }).start();
        latch.await();
        return callback;
    }


    // TODO: 09.12.2016 client.logout should be fixed
    @Test
    @Ignore
    public void testLogout() throws InterruptedException {
        login(username, password, client);
        DataStore<Person> personStore = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);
        Person p = new Person();
        p.setUsername("TestUser");
        save(personStore, p);

        DataStore<Person> userStore = DataStore.collection("users", Person.class, StoreType.SYNC, client);
        Person p2 = new Person();
        p2.setUsername("TestUser2");
        save(userStore, p2);
        UserStore.logout(client);
        assertTrue(!client.isUserLoggedIn());
        assertTrue(client.getSycManager().getCount(Person.COLLECTION) == 0);

    }

    private DefaultPersonKinveyClientCallback save(final DataStore<Person> store, final Person person) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultPersonKinveyClientCallback callback = new DefaultPersonKinveyClientCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                store.save(person, callback);
                Looper.loop();
            }
        }).start();
        latch.await();
        return callback;
    }

    @Test
    public void testLogoutWithNoDatabaseTables() throws InterruptedException {
        login(username, password, client);
        UserStore.logout(client);
        assertTrue(!client.isUserLoggedIn());
        assertTrue(client.getSycManager().getCount(Person.COLLECTION) == 0);
    }

    @Test
    public void testLogoutWithDatabaseTablesButNoAPICalls() throws InterruptedException {
        login(username, password, client);
        DataStore<Person> personStore = DataStore.collection(Person.COLLECTION, Person.class, StoreType.SYNC, client);
        UserStore.logout(client);
        assertTrue(!client.isUserLoggedIn());
        assertTrue(client.getSycManager().getCount(Person.COLLECTION) == 0);
    }


    @Test
    public void testCreateUserAsync() throws InterruptedException {
        String username = System.currentTimeMillis() + "userTest";
        DefaultKinveyClientCallback callback = signUp(username, "newUser2", client);
        assertNotNull(callback.result);
        assertNotNull(callback.result.getUsername());

        assertTrue(client.isUserLoggedIn());
        assertNotNull(callback.result.getUsername().equals(username));

        UserStore.logout(client);
    }

    private DefaultKinveyClientCallback signUp(final String user, final String password, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyClientCallback callback = new DefaultKinveyClientCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                UserStore.signUp(user, password, client, callback);
                Looper.loop();
            }
        }).start();
        latch.await();
        return callback;
    }


    @Test
    public void testFindUserAsync() throws InterruptedException {
        DefaultKinveyClientCallback callback = login(username, password, client);
        User u = callback.result;
        assertTrue(client.isUserLoggedIn());
        assertNotNull(client.activeUser());
        assertTrue(client.activeUser().getId().equals(u.getId()));
        UserStore.logout(client);
    }

    @Test
    @Ignore //No unit test yet
    public void testFindUserAsyncBad() throws InterruptedException {

    }


    @Test
    @Ignore //reason: android UserStore don't have lookupAsync(criteria) method
    public void TestLookupUsersAsync() throws InterruptedException {

    }

    @Test
    @Ignore //reason: android UserStore don't have lookupAsync(criteria) method
    public void testLookupUsersAsyncBad() throws InterruptedException {

    }


    @Test //reason: android UserStore don't have exists() method
    public void testDoesUsernameExist() throws InterruptedException {

    }

/*    private DefaultKinveyUserManagementCallback exists(final String username, final Client client) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final DefaultKinveyUserManagementCallback callback = new DefaultKinveyUserManagementCallback(latch);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                UserStore.exists(username,  client, callback);
                Looper.loop();
            }
        }).start();
        latch.await();
        return callback;
    }*/

    @Test
    public void testUserInitFromCredential() throws InterruptedException {
        Context mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        Client.Builder localBuilder = new Client.Builder(appKey, appSecret, mMockContext);
        Client localClient = localBuilder.build();

        DefaultKinveyClientCallback callback = login(username, password, localClient);
        assertNotNull(callback.result);
        User activeUser = callback.result;

        Context mMockContext2 = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
        Client.Builder localBuilder2 = new Client.Builder(appKey, appSecret, mMockContext2);
        Client localClient2 = localBuilder2.build();
        assertNotNull(localClient2.activeUser());

        assertTrue(activeUser.getId().equals(localClient2.activeUser().getId()));
        assertTrue(activeUser.getAuthToken().equals(localClient2.activeUser().getAuthToken()));
        assertTrue(activeUser.getUsername().equals(localClient2.activeUser().getUsername()));

        UserStore.logout(localClient);
    }



    /*------------------------------------------------------------------------------------------*/

}

