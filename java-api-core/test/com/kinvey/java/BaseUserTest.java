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
package com.kinvey.java;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Hashtable;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.gson.GsonFactory;
import com.kinvey.java.auth.KinveyAuthRequest;
import com.kinvey.java.auth.ThirdPartyIdentity;
import com.kinvey.java.core.KinveyClientRequestInitializer;
import com.kinvey.java.core.KinveyHeaders;
import com.kinvey.java.core.KinveyMockUnitTest;
import com.kinvey.java.dto.BaseUser;
import com.kinvey.java.store.UserStoreRequestManager;
import com.kinvey.java.store.requests.user.Delete;
import com.kinvey.java.store.requests.user.EmailVerification;
import com.kinvey.java.store.requests.user.GetMICAccessToken;
import com.kinvey.java.store.requests.user.ResetPassword;
import com.kinvey.java.store.requests.user.Retrieve;
import com.kinvey.java.store.requests.user.Update;
import com.kinvey.java.testing.MockHttpForMIC;
import com.kinvey.java.testing.MockKinveyAuthRequest;

/**
 * @author mjsalinger
 * @since 2.0
 */
public class BaseUserTest extends KinveyMockUnitTest {

    private UserStoreRequestManager<BaseUser> requestManager;
    private static final String X_KINVEY_CUSTOM_REQUEST_PROPERTIES = "X-Kinvey-Custom-Request-Properties";
    private static final String X_KINVEY_CLIENT_APP_VERSION = "X-Kinvey-Client-App-Version";
    private static final String USER_ID = "userID";

    private void initializeRequestManager(boolean isNeedCreateUser) {
        requestManager = new UserStoreRequestManager<>(getClient(), new MockKinveyAuthRequest.MockBuilder(getClient().getRequestFactory().getTransport(),
                getClient().getJsonFactory(), "mockAppKey","mockAppSecret", null));
        if (isNeedCreateUser) {
            requestManager.getClient().setActiveUser(new BaseUser());
        }
    }

    public void testInitializeUser() {
        UserStoreRequestManager<BaseUser> user = new UserStoreRequestManager<>(getClient(), new MockKinveyAuthRequest.MockBuilder(getClient().getRequestFactory().getTransport(),
                getClient().getJsonFactory(), "mockAppKey","mockAppSecret",null));
        assertNotNull(user);
        assertEquals(getClient(),user.getClient());
        assertEquals(getClient().getKinveyRequestInitializer(), user.getClient().getKinveyRequestInitializer());
    }

    public void testInitializeUserNullClient() {
        try {
            UserStoreRequestManager<BaseUser> user = new UserStoreRequestManager<>(null, new MockKinveyAuthRequest.MockBuilder(getClient().getRequestFactory().getTransport(),
                    getClient().getJsonFactory(), "mockAppKey","mockAppSecret",null));
            fail("NullPointerException should be thrown");
        } catch (NullPointerException ex) {}
    }

    public void testInitializeNoBuilder() {
        try {
            UserStoreRequestManager user = new UserStoreRequestManager(getClient(), null);
            fail("NullPointerException should be thrown");
        } catch (NullPointerException ex) {}
    }


    public void testLoginKinveyUserNullUsername() throws IOException {
        initializeRequestManager(false);
        try {
            requestManager.loginBlocking(null, "myPassword").execute();
            fail("NullPointerException should be thrown");
        } catch (NullPointerException ex) {}
    }

    public void testLoginKinveyUserNullPassword() throws IOException {
        initializeRequestManager(false);
        try {
            requestManager.loginBlocking("myUserName", null).execute();
            fail("NullPointerException should be thrown");
        } catch (NullPointerException ex) {}
    }


    public void testLoginFacebookUserNullArguments() throws IOException {
        initializeRequestManager(false);
        try {
            requestManager.login(ThirdPartyIdentity.Type.FACEBOOK, null).execute();
            fail("NullPointerException should be thrown");
        } catch (NullPointerException ex) {}
    }

    public void testLoginFacebookTooFewArguments() throws IOException {
        initializeRequestManager(false);
        try {
            requestManager.login(ThirdPartyIdentity.Type.FACEBOOK, new String[] {}).execute();
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException ex) {}
    }

    public void testLoginFacebookTooManyArguments() throws IOException {
        initializeRequestManager(false);
        try {
            requestManager.login(ThirdPartyIdentity.Type.FACEBOOK, new String[] {"arg1","arg2"}).execute();
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException ex) {}
    }



    public void testDeleteHardDeleteTrue() throws IOException {
        initializeRequestManager(true);
        BaseUser baseUser = requestManager.getClient().getActiveUser();
        baseUser.setId("testUser");
        Delete del = requestManager.deleteBlocking(true);
        assertEquals(requestManager.getClient().getActiveUser().getId(), del.get(USER_ID).toString());
        assertEquals(true, del.get("hard"));
    }

    public void testDeleteHardDeleteFalse() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
        Delete del = requestManager.deleteBlocking(false);
        assertEquals(requestManager.getClient().getActiveUser().getId(),del.get(USER_ID).toString());
        assertEquals(false,del.get("hard"));
        assertEquals("DELETE",del.getRequestMethod());
    }

    public void testDeleteNullUser() throws IOException {
        initializeRequestManager(false);
        try {
            Delete del = requestManager.deleteBlocking(true);
            fail("NullPointerException should be thrown.");
        } catch (NullPointerException ex) {}
    }

    public void testRetrieve() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
        Retrieve ret = requestManager.retrieveBlocking();
        assertEquals(requestManager.getClient().getActiveUser().getId(),ret.get(USER_ID).toString());
        assertEquals("GET", ret.getRequestMethod());
    }

    public void testRetrieveNullUser() throws IOException {
        initializeRequestManager(false);
        try {
            Retrieve ret = requestManager.retrieveBlocking();
            fail("NullPointerException should be thrown.");
        } catch (NullPointerException ex) {}
    }

    public void testUpdate() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
        Update update = requestManager.updateBlocking();
        assertEquals(requestManager.getClient().getActiveUser().getId(),update.get(USER_ID).toString());
        assertEquals("PUT", update.getRequestMethod());
    }

    public void testUpdateNullUser() throws IOException {
        initializeRequestManager(false);
        try {
            Update update = requestManager.updateBlocking();
            fail("NullPointerException should be thrown.");
        } catch (NullPointerException ex) {}
    }

    public void testResetPassword() throws IOException {
        initializeRequestManager(true);
        BaseUser baseUser = requestManager.getClient().getActiveUser();
        baseUser.setId("testUser");
        requestManager.getClient().getActiveUser().setUsername("test");
        ResetPassword pwd = requestManager.resetPasswordBlocking(requestManager.getClient().getActiveUser().getUsername());
        assertEquals(requestManager.getClient().getActiveUser().getUsername(),pwd.get(USER_ID).toString());
        assertEquals("POST", pwd.getRequestMethod());
    }

    public void testResetPasswordNullUser() throws IOException {
        initializeRequestManager(false);
        try {
            ResetPassword pwd = requestManager.resetPasswordBlocking(null);
            fail("NullPointerException should be thrown.");
        } catch (NullPointerException ex) {}
    }

    public void testEmailVerification() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
        EmailVerification email = requestManager.sendEmailVerificationBlocking();
        assertEquals(requestManager.getClient().getActiveUser().getId(),email.get(USER_ID).toString());
        assertEquals("POST", email.getRequestMethod());
    }

    public void testEmailVerificationNullUser() throws IOException {
        initializeRequestManager(false);
        try {
            EmailVerification email = requestManager.sendEmailVerificationBlocking();
            fail("NullPointerException should be thrown.");
        } catch (NullPointerException ex) {}
    }

    public void testUserCustomVersion() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
    	requestManager.getClient().setClientAppVersion("1.2.3");
    	Retrieve request = requestManager.retrieveBlocking();
    	Object header = request.getRequestHeaders().get(X_KINVEY_CLIENT_APP_VERSION);
    	assertEquals("1.2.3", (String) header);
    }

    public void testUserCustomVesionAsNumber() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
        requestManager.getClient().setClientAppVersion(1, 2, 3);
        Retrieve request = requestManager.retrieveBlocking();
        Object header = request.getRequestHeaders().get(X_KINVEY_CLIENT_APP_VERSION);
        assertEquals("1.2.3", (String) header);

    }

    public void testUserCustomHeader() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
    	GenericJson custom = new GenericJson();
    	custom.put("First", 1);
    	custom.put("Second", "two");
    	requestManager.getClient().setCustomRequestProperties(custom);
    	Retrieve request = requestManager.retrieveBlocking();
    	Object header = request.getRequestHeaders().get(X_KINVEY_CUSTOM_REQUEST_PROPERTIES);
    	assertEquals("{\"First\":1,\"Second\":\"two\"}", (String) header);

    }

    public void testUserCustomHeaderOverload() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");

        requestManager.getClient().setCustomRequestProperty("First", 1);
        requestManager.getClient().setCustomRequestProperty("Second", "two");

        Retrieve request = requestManager.retrieveBlocking();
        Object header = request.getRequestHeaders().get(X_KINVEY_CUSTOM_REQUEST_PROPERTIES);
        assertEquals("{\"First\":1,\"Second\":\"two\"}", (String) header);

    }

    public void testUserCustomVersionNull() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
    	requestManager.getClient().setClientAppVersion(null);
    	Retrieve request = requestManager.retrieveBlocking();
    	Object header = request.getRequestHeaders().get(X_KINVEY_CLIENT_APP_VERSION);
    	assertEquals(null, header);
    }

    public void testUserCustomHeaderNull() throws IOException {
        initializeRequestManager(true);
        requestManager.getClient().getActiveUser().setId("testUser");
        requestManager.getClient().clearCustomRequestProperties();
    	Retrieve request = requestManager.retrieveBlocking();
    	Object header = request.getRequestHeaders().get(X_KINVEY_CUSTOM_REQUEST_PROPERTIES);
    	assertEquals(null, header);
    }

    public void testCustomMICBase() throws IOException{
    	initializeRequestManager(false);
    	getClient().setMICHostName("https://www.google.com");

    	try{
    		getClient().setMICHostName("http://www.google.com");
    		fail("Library should throw an exception when setting non https base url for MIC");
    	}catch(Exception e){}


        // Construct the full token path. Might be affected by the MIC API version.
        String fullTokenPath = "https://www.google.com/" + getClient().getMICApiVersion() + "/oauth/token";
    	GetMICAccessToken getToken = requestManager.getMICToken("myCODE", null);
        assertEquals(fullTokenPath, getToken.buildHttpRequest().getUrl().toString());
    }

    public void testMICLoginWithAccessToken() throws IOException{

    	requestManager = new UserStoreRequestManager<>(getClient(new MockHttpForMIC()), new KinveyAuthRequest.Builder<>(new MockHttpForMIC(),
                new GsonFactory(),"https://baas.kinvey.com",  "mockAppKey","mockAppSecret",null));

    	GetMICAccessToken token = requestManager.getMICToken("MyToken", null);
    	GenericJson result =  (GenericJson) token.execute();

        //check
/*        if (getClient().isUserLoggedIn()) {
            requestManager.logout().execute();
        }*/
        BaseUser ret = requestManager.loginMobileIdentityBlocking(result.get("access_token").toString()).execute();

        getClient().setActiveUser(ret);

        assertEquals(true, getClient().isUserLoggedIn());
    }

    public void testMICAPIVersionAppendsV() throws IOException{
        initializeRequestManager(false);
        getClient().setMICApiVersion("2");
        assertEquals(getClient().getMICApiVersion(), "v2");
    }

    public void testClientAppVersionHeader() throws IOException, NoSuchFieldException, IllegalAccessException {
        String clientAppVersion = "1.2.3";
        getClient().setClientAppVersion(clientAppVersion);
        UserStoreRequestManager user = new UserStoreRequestManager(getClient(), createBuilder(getClient()));
        assertNotNull(user);
        UserStoreRequestManager.LoginRequest loginRequest = user.createBlocking("test_name", "test_login").buildAuthRequest();
        Field kinveyAuthRequestField = loginRequest.getClass().getDeclaredField("request"); //NoSuchFieldException
        kinveyAuthRequestField.setAccessible(true);
        KinveyAuthRequest request = (KinveyAuthRequest) kinveyAuthRequestField.get(loginRequest);
        Field kinveyHeadersField = request.getClass().getDeclaredField("kinveyHeaders"); //NoSuchFieldException
        kinveyHeadersField.setAccessible(true);
        KinveyHeaders kinveyHeaders = (KinveyHeaders) kinveyHeadersField.get(request);
        String clientAppVersionHeader = (String) kinveyHeaders.get("X-Kinvey-Client-App-Version");
        assertEquals(clientAppVersion, clientAppVersionHeader);
    }

    public void testClientAppVersionHeaderDefault() throws IOException, NoSuchFieldException, IllegalAccessException {
        UserStoreRequestManager user = new UserStoreRequestManager(getClient(), createBuilder(getClient()));
        assertNotNull(user);
        UserStoreRequestManager.LoginRequest loginRequest = user.createBlocking("test_name", "test_login").buildAuthRequest();
        Field kinveyAuthRequestField = loginRequest.getClass().getDeclaredField("request"); //NoSuchFieldException
        kinveyAuthRequestField.setAccessible(true);
        KinveyAuthRequest request = (KinveyAuthRequest) kinveyAuthRequestField.get(loginRequest);
        Field kinveyHeadersField = request.getClass().getDeclaredField("kinveyHeaders"); //NoSuchFieldException
        kinveyHeadersField.setAccessible(true);
        KinveyHeaders kinveyHeaders = (KinveyHeaders) kinveyHeadersField.get(request);
        String clientAppVersionHeader = (String) kinveyHeaders.get("X-Kinvey-Client-App-Version");
        assertNull(clientAppVersionHeader);
    }

    private KinveyAuthRequest.Builder createBuilder(AbstractClient client) {
        String appKey = ((KinveyClientRequestInitializer) client.getKinveyRequestInitializer()).getAppKey();
        String appSecret = ((KinveyClientRequestInitializer) client.getKinveyRequestInitializer()).getAppSecret();

        return new KinveyAuthRequest.Builder(client.getRequestFactory().getTransport(),
                client.getJsonFactory(), client.getBaseUrl(), appKey, appSecret, null);
    }

}