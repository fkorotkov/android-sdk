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
package com.kinvey.android.store;

import java.io.IOException;

import android.content.Intent;
import android.net.Uri;

import com.google.api.client.json.GenericJson;
import com.kinvey.android.AsyncClientRequest;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyMICCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.callback.KinveyUserDeleteCallback;
import com.kinvey.android.callback.KinveyUserListCallback;
import com.kinvey.android.callback.KinveyUserManagementCallback;
import com.kinvey.android.ui.MICLoginActivity;
import com.kinvey.java.AbstractClient;
import com.kinvey.java.Query;
import com.kinvey.java.auth.Credential;
import com.kinvey.java.auth.KinveyAuthRequest;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.core.KinveyClientRequestInitializer;
import com.kinvey.java.dto.User;
import com.kinvey.java.store.UserStore;
import com.kinvey.java.store.requests.user.LogoutRequest;

/**
 * Maintains definitions of all asyncronous user operation methods, this class is meant to be extended.
 * <p/>
 *
 * Wraps the {@link com.kinvey.java.store.UserStore} public methods in asynchronous functionality using native Android AsyncTask.
 *
 * <p>
 * This functionality can be accessed through the {@link com.kinvey.android.Client#userStore()} convenience method.
 * </p>
 *
 * <p>
 * Methods in this API use either {@link com.kinvey.android.callback.KinveyUserCallback} for authentication, login, and
 * user creation, or the general-purpose {@link com.kinvey.java.core.KinveyClientCallback} used for User data retrieval,
 * updating, and management.
 * </p>
 *
 * <p>
 * Login sample:
 * <pre>
 * {@code
public void submit(View view) {
kinveyClient.user().login(mEditUserName.getText().toString(), mEditPassword.getText().toString(),
new KinveyUserCallback() {
public void onFailure(Throwable t) {
CharSequence text = "Wrong username or password.";
Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
}
public void onSuccess(User u) {
CharSequence text = "Welcome back," + u.getUsername() + ".";
Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
LoginActivity.this.startActivity(new Intent(LoginActivity.this,
SessionsActivity.class));
LoginActivity.this.finish();
}
});
}
 * </pre>
 *
 * </p>
 * <p>
 * Saving user data sample:
 * <pre>
 * {@code
User user = kinveyClient.user();
user.put("fav_food", "bacon");
user.update(new KinveyClientCallback<User.Update>() {

public void onFailure(Throwable e) { ... }

public void onSuccess(User u) { ... }
});
}
 * </pre>
 * </p>
 *
 * <p>This class is not thread-safe.</p>
 * @author edwardf
 */
public abstract class AbstractAsyncUserStore<T extends User> extends UserStore<T> {

    /**
     * Flag indicating if a logout operation should clear all local storage
     *
     */
    private boolean clearStorage = true;
    
    /**
     * The callback for the MIC login, this is maintained because it used by the broadcast reciever after the redirect
     */
    protected KinveyUserCallback<T> MICCallback;
        
    /**
     * Base constructor requires the client instance and a {@link com.kinvey.java.auth.KinveyAuthRequest.Builder} to be passed in.
     * <p>
     * {@link com.kinvey.java.core.AbstractKinveyClient#initializeRequest(com.kinvey.java.core.AbstractKinveyClientRequest)} is used to initialize all
     * requests constructed by this api.
     * </p>
     *
     * @param client instance of current client
     * @throws NullPointerException if the client parameter and KinveyAuthRequest.Builder is non-null
     */
    public AbstractAsyncUserStore(AbstractClient client, Class<T> userClass,  KinveyAuthRequest.Builder builder) {
        super(client, userClass, builder);
    }


    /**
     * Asynchronous implicit user login.
     * <p>
     * Constructs an asynchronous request to log in an implicit (non-named) user and returns the associated User object
     * via a KinveyUserCallback.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     * {@code
    kinveyClient.user().login(new KinveyUserCallback() {
    public void onFailure(Throwable t) { ... }
    public void onSuccess(User u) {
    CharSequence text = "Welcome back!";
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    });
    }
     * </pre>
     * </p>
     *
     * @param callback {@link KinveyUserCallback} that returns a valid user object
     */
    public void login(KinveyClientCallback<T> callback) {
        new Login(callback).execute();
    }

    /**
     * Asynchronous Kinvey user login.
     * <p>
     * Constructs an asynchronous request to log in a Kinvey user with username and password, and returns the associated
     * User object via a KinveyUserCallback.
     * </p>
     *
     * <p>
     Sample Usage:
     * <pre>
     * {@code
    kinveyClient.user().login(mEditUserName.getText().toString(), mEditPassword.getText().toString(),
    new KinveyUserCallback() {
    public void onFailure(Throwable t) {
    CharSequence text = "Wrong username or password.";
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    public void onSuccess(User u) {
    CharSequence text = "Welcome back," + u.getUsername() + ".";
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    LoginActivity.this.startActivity(new Intent(LoginActivity.this,
    SessionsActivity.class));
    LoginActivity.this.finish();
    }
    });
    }
     * </pre>
     * </p>
     *
     * @param userid userID of the Kinvey User
     * @param password password of the Kinvey user
     * @param callback {@link KinveyUserCallback} that returns a valid user object
     */
    public void login(String userid, String password, KinveyClientCallback<T> callback) {
        new Login(userid, password, callback).execute();
    }

    /**
     * Asynchronous Facebook login.
     * <p>Constructs an asynchronous request to log in a Facebook user and returns the associated User object via a
     * KinveyUserCallback.  A valid Facebook access token must be obtained via the Facebook OAuth API and passed to this
     * method.
     * </p>
     * <p>
     * Sample Usage:
     * <pre>
     kinveyClient.user().loginFacebook(accessToken, new KinveyUserCallback() {

     public void onFailure(Throwable e) {
     error(progressDialog, "Kinvey: " + e.getMessage());
     Log.e(TAG, "failed Kinvey facebook login", e);
     }

     public void onSuccess(User u) {
     Log.d(TAG, "successfully logged in with facebook");
     }
     });
     * </pre>
     * </p>
     *
     * @param accessToken Facebook-generated access token.
     * @param callback {@link KinveyUserCallback} that returns a valid user object
     */
    public void loginFacebook(String accessToken, KinveyClientCallback<T> callback) {
        new Login(accessToken, LoginType.FACEBOOK, callback).execute();
    }

    /**
     * Asynchronous Google login.
     *
     * <p>
     * Constructs an asynchronous request to log in a Google user and returns the associated User object via a
     * KinveyUserCallback.  A valid Google access token must be obtained via the Google OAuth API and passed to this
     * method.
     * </p>
     * <p>
     * Sample Usage:
     * <pre>
     {@code
     kinveyClient.user().loginGoogle(accessToken, new KinveyUserCallback() {

     public void onFailure(Throwable e) {
     error(progressDialog, "Kinvey: " + e.getMessage());
     Log.e(TAG, "failed Kinvey facebook login", e);
     }

     public void onSuccess(User u) {
     Log.d(TAG, "successfully logged in with facebook");
     }
     });
     }
     * </pre>
     * </p>
     *
     * @param accessToken Google-generated access token.
     * @param callback {@link KinveyUserCallback} that contains a valid logged in user
     */
    public void loginGoogle(String accessToken, KinveyClientCallback<T> callback)  {
        new Login(accessToken, LoginType.GOOGLE, callback).execute();
    }

    /**
     * Login to Kinvey Services using a Salesforce identity
     *
     * @param accessToken - provided by salesforce, after successful login
     * @param clientid - client id used by salesforce
     * @param refreshToken - provided by salesforce, after successful login
     * @param id - the salesforce id of the user
     * @param callback - {@link KinveyUserCallback} that contains a valid logged in user
     */
    public void loginSalesForce (String accessToken, String clientid, String refreshToken, String id, KinveyClientCallback<T> callback){
        new Login(accessToken, clientid, refreshToken, id, callback).execute();

    }


    /**
     * Login to Kinvey Service using Kinvey Mobile Identity Connect Service
     *
     * @param accessToken - a MIC access token
     * @param callback -
     */
    public void loginMobileIdentity(String accessToken, KinveyUserCallback callback){
        new Login(accessToken, LoginType.MOBILE_IDENTITY, callback).execute();
    }

    /**
     * Login to Kinvey services using a Kinvey user's _id and their valid Kinvey Auth Token.  This method is provided
     * to allow for cross-platform login, by reusing a session provided with another client library (or the REST api).
     *
     * @param userId the _id field of the user to login
     * @param authToken a valid Kinvey Auth token
     * @param callback {@link KinveyUserCallback} that contains a valid logged in user
     * @return a LoginRequest ready to be executed
     * @throws IOException
     */
    public void loginKinveyAuthToken(String userId, String authToken, KinveyClientCallback<T> callback){
        new LoginKinveyAuth(userId, authToken, callback).execute();
    }



    /**
     * Asynchronous Twitter login.
     *
     * <p>
     * Constructs an asynchronous request to log in a Twitter user and returns the associated User object via a
     * KinveyUserCallback.  A valid Twitter access token, access secret, consumer key, and consumer secret must be
     * obtained via the Twitter OAuth API and passed to this method.
     * </p>
     * <p>
     * Sample Usage:
     * <pre>
     * {@code
    kinveyClient.user().loginTwitter(accessToken, accessSecret, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET,
            new KinveyUserCallback() {

    public void onFailure(Throwable e) {
    Log.e(TAG, "Failed Kinvey login", e)
    };

    public void onSuccess(User r) {
    Log.e(TAG, "Successfully logged in via Twitter");
    }
    });
    }
     *
     * @param accessToken Twitter-generated access token
     * @param accessSecret Twitter-generated access secret
     * @param consumerKey Twitter supplied developer consumer key
     * @param consumerSecret Twitter supplied developer consumer secret
     * @param callback {@link KinveyUserCallback} that returns a valid user object
     */
    public void loginTwitter(String accessToken, String accessSecret, String consumerKey, String consumerSecret,
                             KinveyClientCallback<T> callback)  {
        new Login(accessToken, accessSecret, consumerKey, consumerSecret, LoginType.TWITTER, callback).execute();
    }

    /**
     *  Asynchronous Linked In login.
     *
     * <p>
     * Constructs an asynchronous request to log in a Linked In user and returns the associated User object via a
     * KinveyUserCallback.  A valid Linked In access token, access secret, consumer key, and consumer secret must be
     * obtained via the Linked In OAuth API and passed to this method.
     * </p>
     * <p>
     * Sample Usage:
     * <pre>
     * {@code
    kinveyClient.user().loginLinkedIn(accessToken, accessSecret, LINKEDIN_CONSUMER_KEY, LINKEDIN_CONSUMER_SECRET,
            new KinveyUserCallback() {

    public void onFailure(Throwable e) {
    Log.e(TAG, "Failed Kinvey login", e)
    };

    public void onSuccess(User r) {
    Log.e(TAG, "Successfully logged in via Linked In");
    }
    });
    }
     *
     * @param accessToken Linked In-generated access token
     * @param accessSecret Linked In-generated access secret
     * @param consumerKey Linked In supplied developer consumer key
     * @param consumerSecret Linked In supplied developer consumer secret
     * @param callback {@link KinveyUserCallback} that returns a valid user object
     */
    public void loginLinkedIn(String accessToken, String accessSecret, String consumerKey, String consumerSecret,
                              KinveyClientCallback<T> callback) {
        new Login(accessToken, accessSecret, consumerKey, consumerSecret, LoginType.LINKED_IN, callback).execute();
    }


    /**
     * Asyncronous login using a Kinvey Auth Link
     *
     * @param accessToken The access token provided by the auth source
     * @param refreshToken The refresh token provided by the auth source
     * @param callback {@link KinveyUserCallback} that returns a valid user object
     */
    public void loginAuthLink(String accessToken, String refreshToken, KinveyClientCallback<T> callback) {
        new Login(accessToken, refreshToken, LoginType.AUTH_LINK, callback).execute();
    }

    /**
     * Asynchronous Retrieve Metadata
     *
     * <p>
     * Convenience method for retrieving user metadata and updating the current user with the metadata.  Used
     * when initializing the client.
     * </p>
     *
     * @param callback KinveyUserCallback
     */
    public void retrieveMetadata(KinveyClientCallback<T> callback) {
        new RetrieveMetaData(callback).execute();

    }

    /**
     * Asynchronous method to create a new Kinvey User.
     * <p>
     * Constructs an asynchronous request to create a Kinvey user with username and password, and returns the associated
     * User object via a KinveyUserCallback.  All metadata that is added to the user object prior to creating the user
     * will be persisted to the Kinvey backend.
     * </p>
     *
     * <p>
     * Sample Usage:
     * <pre>
     * {@code
    kinveyClient.user().put("State","MA");
    kinveyClient.user().put("Age", 25);
    kinveyClient.user().create(mEditUserName.getText().toString(), mEditPassword.getText().toString(),
    new KinveyUserCallback() {

    public void onFailure(Throwable t) {
    CharSequence text = "Unable to create account.";
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void onSuccess(User u) {
    CharSequence text = "Welcome " + u.getUsername() + ".";
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    });
    }
     * </pre>
     * </p>
     *
     * @param username username of the Kinvey User
     * @param password password of the Kinvey user
     * @param callback {@link KinveyUserCallback} containing a new User instance.
     */
    public void create(String username, String password, KinveyClientCallback<T> callback) {
        new Create(username, password, callback).execute();
    }

    /**
     * Get the Client associated with this instance.
     *
     * @return
     */
    @Override
    public Client getClient() {
        return (Client) super.getClient();
    }

    /**
     * Asynchronous Call to Delete the current user from the Kinvey backend
     * <p>
     * Constructs an asynchronous request to delete the current Kinvey user.  The hardDelete flag determines whether
     * the user is simply marked as inactive or completely erased from the Kinvey backend.
     * </p>
     * <p>
     * Sample Usage:
     * </p>
     * <pre>
     * {@code
    User user = kinveyClient.user();
    user.delete(new KinveyUserDeleteCallback() {
    public void onFailure(Throwable e) { ... }
    public void onSuccess(Void v) { ... }
    });
    }
     * </pre>
     *
     * @param hardDelete Erases user from Kinvey backend if true; inactivates the user if false.
     * @param callback {@link KinveyUserDeleteCallback}.
     */
    public void delete(Boolean hardDelete, KinveyUserDeleteCallback callback) {
        new Delete(hardDelete, callback).execute();
    }

    /**
     * Asynchronous Call to Retrieve (refresh) the current user
     * <p>
     * Constructs an asynchronous request to refresh current user's data via the Kinvey back-end.
     * </p>
     * <p>
     * Sample Usage:
     * </p>
     * <pre>
     {@code
     User user = kinveyClient.user();
     user.retrieve(new KinveyUserCallback() {
     public void onFailure(Throwable e) { ... }
     public void onSuccess(User result) { ... }
     });
     }
     * </pre>
     *
     * @param callback {@link KinveyUserCallback} containing a refreshed User instance.
     * @param <T>
     */
    public<T> void retrieve(KinveyClientCallback<T> callback) {
        new Retrieve(callback).execute();
    }


    /**
     * Asynchronous call to retrive (refresh) the current user, and resolve KinveyReferences
     * <p>
     * Constructs an asynchronous request to refresh current user's data via the Kinvey back-end.
     * </p>
     * <p>
     * Sample Usage:
     * </p>
     * <pre>
     {@code
     User user = kinveyClient.user();
     user.retrieve(new String[]{"myKinveyReferencedField"}, new KinveyUserCallback() {
     public void onFailure(Throwable e) { ... }
     public void onSuccess(User result) { ... }
     });
     }
     * </pre>
     *
     * @param resolves an array of json keys maintaining KinveyReferences to be resolved
     * @param callback {@link KinveyUserCallback} containing refreshed user instance
     */
    public void retrieve(String[] resolves, KinveyClientCallback<T> callback){
        new Retrieve(resolves, callback).execute();
    }




    /**
     * Asynchronous call to retrive (refresh) the users by query, and resolve KinveyReferences
     * <p>
     * Constructs an asynchronous request to retrieve User objects via a Query.
     * </p>
     * <p>
     * Sample Usage:
     * </p>
     * <pre>
     * {@code
    User user = kinveyClient.user();
    user.retrieve(Query query, new String[]{"myKinveyReferenceField"}, new KinveyUserListCallback() {
    public void onFailure(Throwable e) { ... }
    public void onSuccess(User[] result) { ... }
    });
    }
     * </pre>
     *
     *
     *
     * @param query the query to execute defining users to return
     * @param resolves an array of json keys maintaining KinveyReferences to be resolved
     * @param callback {@link com.kinvey.android.callback.KinveyUserListCallback} containing an array of queried users
     */
    public void retrieve(Query query, String[] resolves, KinveyUserListCallback callback){
        new Retrieve(query, resolves, callback).execute();
    }

    /**
     * Asynchronous Call to Retrieve users via a Query
     * <p>
     * Constructs an asynchronous request to retrieve User objects via a Query.
     * </p>
     * <p>
     * Sample Usage:
     * </p>
     * <pre>
     * {@code
    User user = kinveyClient.user();
    user.retrieve(Query query, new KinveyUserListCallback() {
    public void onFailure(Throwable e) { ... }
    public void onSuccess(User[] result) { ... }
    });
    }
     * </pre>
     *
     * @param callback {@link com.kinvey.android.callback.KinveyUserListCallback} for retrieved users
     */
    public void retrieve(Query q, KinveyListCallback<T> callback) {
        new Retrieve(q, callback).execute();
    }


    /***
     *
     * Login with the MIC service, using the oauth flow.  This method provides a URL to render containing a login page.
     *
     * @param redirectURI
     * @param callback
     */
    public void loginWithAuthorizationCodeLoginPage(String redirectURI, KinveyMICCallback callback){
    	//return URL for login page
    	//https://auth.kinvey.com/oauth/auth?client_id=<your_app_id>i&redirect_uri=<redirect_uri>&response_type=code
    	String appkey = ((KinveyClientRequestInitializer) getClient().getKinveyRequestInitializer()).getAppKey();
    	String host = MICHostName;
    	if (MICApiVersion != null && MICApiVersion.length() > 0){
    		host = MICHostName + MICApiVersion + "/";
    	}
    	String myURLToRender = host + "oauth/auth?client_id=" + appkey + "&redirect_uri=" + redirectURI + "&response_type=code";
    	//keep a reference to the callback and redirect uri for later
    	this.MICCallback = callback;
    	this.MICRedirectURI = redirectURI;

    	if (callback != null){
    		callback.onReadyToRender(myURLToRender);
    	}

    }

    /**
     * Used by the MIC login flow, this method should be called after a successful login in the onNewItent Method of your activity.  See the MIC guide for more information.
     *
     * @param intent The intent provided to the application from the redirect
     */
    public void onOAuthCallbackRecieved(Intent intent){
    	if (intent == null || intent.getData() == null){
    		return;
    	}
		final Uri uri = intent.getData();
		String accessToken = uri.getQueryParameter("code");
		if (accessToken == null){
			return;
		}
		getMICAccessToken(accessToken);
    }

    /***
     *
     * Login with the MIC service, using the oauth flow.  This method provides direct login, without rending a login page.
     *
     * @param username
     * @param password
     * @param redirectURI
     * @param callback
     */
    public void loginWithAuthorizationCodeAPI(String username, String password, String redirectURI, KinveyUserCallback callback){
    	this.MICCallback = callback;
    	this.MICRedirectURI = redirectURI;

    	new PostForTempURL(username, password, callback).execute();
    }

    /**
     * Posts for a MIC login Access token
     *
     * @param token the access code returned from the MIC Auth service
     */
    public void getMICAccessToken(String token){
    	new PostForAccessToken(token, MICCallback).execute();
    }

    /***
     * Initiate the MIC login flow with an Activity containing a Webview
     *
     * @param redirectURI
     * @param callback
     */
    public void presentMICLoginActivity(String redirectURI, final KinveyUserCallback<T> callback){

        loginWithAuthorizationCodeLoginPage(redirectURI, new KinveyMICCallback<T>() {
            @Override
            public void onReadyToRender(String myURLToRender) {
                Intent i = new Intent(getClient().getContext(), MICLoginActivity.class);
                i.putExtra(MICLoginActivity.KEY_LOGIN_URL, myURLToRender);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                getClient().getContext().startActivity(i);
            }

            @Override
            public void onSuccess(T result) {
                if(callback != null){
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if(callback != null){
                    callback.onFailure(error);
                }
            }
        });
    }


    /**
     * Logs out the current user and clears the local sqllite3 storage
     *
     * @return a Logout Request, ready to have *.execute() called
     */
    @Override
    public LogoutRequest logout() {
        if (clearStorage){
            getClient().performLockDown();
        }
        return super.logout();

    }


    /**
     * Set a flag to allow local offline storage to persist after calls to logout.
     * <p/>
     * Only use this method if each device will have a guaranteed consistent user and there are no concerns about security
     */
    public void keepOfflineStorageOnLogout(){
        clearStorage = false;
    }


    /**
     * Asynchronous Call to Save the current user
     * <p>
     * Constructs an asynchronous request to save the current Kinvey user.
     * </p>
     * <p>
     * Sample Usage:
     * </p>
     * <pre>
     {@code
     User user = kinveyClient.user();
     user.update(new KinveyUserCallback() {
     public void onFailure(Throwable e) { ... }
     public void onSuccess(User result) { ... }
     });
     }
     * </pre>
     *
     * @param callback {@link KinveyUserCallback} containing an updated User instance.
     */
    public void update(KinveyClientCallback<T> callback) {
        new Update(AbstractAsyncUserStore.this, callback).execute();
    }

    /**
     * Asynchronous Call to Save a given user
     * <p>
     * Constructs an asynchronous request to save a provided Kinvey user.
     * </p>
     * Ensure you have configured backend to allow for `Full` permissions on User edits through the console.
     * <p>
     * Sample Usage:
     * </p>
     * <pre>
     {@code
     kinveyClient.user().retrieve(new Query(), new KinveyUserListCallback(){
     public void onFailure(Throwable e) { ... }
     public void onSuccess(User[] result) {
     for (User u : result){
     kinveyClient.User().update(u, new KinveyUserCallback() {
     public void onFailure(Throwable e) { ... }
     public void onSuccess(User result) { ... }
     });
     }
     }
     });
     }
     * </pre>
     *
     * @param callback {@link KinveyUserCallback} containing an updated User instance.
     */
    public void update(UserStore<T> user, KinveyClientCallback<T> callback){
        new Update(user, callback).execute();
    }

    /**
     * Asynchronous Call to initiate a Password Reset request
     * <p>
     * The reset password request initiates a server-side process to reset a user's password.  Once executed, a
     * success callback is initiated.  The user is then emailed by the server to receive the password reset.  The user's
     * email address must be stored in a property named 'email' in the User collection.
     * </p>
     * <p>Sample Usage:
     * <pre>
     * {@code
    kinveyClient.resetPassword(new KinveyClientCallback<User>() {
    public void onFailure(Throwable e) { ... }
    public void onSuccess() { ... }
    });
    }
     * </pre></p>
     * @param callback {@link com.kinvey.android.callback.KinveyUserManagementCallback}
     */
    public void resetPassword(String username, KinveyUserManagementCallback callback) {
        new ResetPassword(username, callback).execute();
    }

    /**
     * Asynchronous Call to initiate an Email Verification request
     * <p>
     * The email verification request initiates a server-side process to verify a user's email.  Once executed, a
     * success callback is initiated.  The user is then emailed by the server to receive the email verification.  The user's
     * email address must be stored in a property named 'email' in the User collection.
     * </p>
     * <p>Sample Usage:
     * <pre>
     * {@code
    kinveyClient.sendEmailVerification(new KinveyClientCallback<User>() {
    public void onFailure(Throwable e) { ... }
    public void onSuccess(Void result) { ... }
    });
     * </pre></p>
     * @param callback {@link com.kinvey.android.callback.KinveyUserManagementCallback}
     */
    public void sendEmailVerification(KinveyUserManagementCallback callback) {
        new EmailVerification(callback).execute();
    }



    private class RetrieveMetaData extends AsyncClientRequest<T> {

        private RetrieveMetaData(KinveyClientCallback<T> callback) {
            super(callback);
        }

        @Override
        protected T executeAsync() throws IOException, InstantiationException, IllegalAccessException {
            return AbstractAsyncUserStore.this.retrieveMetadataBlocking();
        }
    }

    private class Update extends AsyncClientRequest<User> {

        UserStore user = null;

        private Update(UserStore user, KinveyClientCallback callback){
            super(callback);
            this.user = user;
        }

        @Override
        protected User executeAsync() throws IOException {
            return AbstractAsyncUserStore.this.updateBlocking(user.getCurrentUser()).execute();
        }
    }



    private class ResetPassword extends AsyncClientRequest<Void> {

        String username;

        private ResetPassword(String username, KinveyClientCallback callback) {
            super(callback);
            this.username = username;
        }

        @Override
        protected Void executeAsync() throws IOException {
            AbstractAsyncUserStore.this.resetPasswordBlocking(this.username).execute();
            return null;
        }
    }

    private class EmailVerification extends AsyncClientRequest<Void> {


        private EmailVerification(KinveyClientCallback callback) {
            super(callback);
        }

        @Override
        protected Void executeAsync() throws IOException {
            AbstractAsyncUserStore.this.sendEmailVerificationBlocking().execute();
            return null;
        }
    }

    private class LoginKinveyAuth extends AsyncClientRequest<T> {

        private String authToken;
        private String userID;

        private LoginKinveyAuth(String userId, String authToken, KinveyClientCallback<T> callback){
            super(callback);
            this.userID = userId;
            this.authToken = authToken;
        }


        @Override
        protected T executeAsync() throws IOException, InstantiationException, IllegalAccessException {
            return AbstractAsyncUserStore.this.loginKinveyAuthTokenBlocking(userID,  authToken).execute();

        }
    }

    private class Login extends AsyncClientRequest<T> {

        String username;
        String password;
        String accessToken;
        String refreshToken;
        String accessSecret;
        String consumerKey;
        String consumerSecret;
        Credential credential;
        LoginType type;

        //Salesforce...
        String id;
        String client_id;

        private Login(KinveyClientCallback callback) {
            super(callback);
            this.type = LoginType.IMPLICIT;
        }

        private Login(String username, String password, KinveyClientCallback callback) {
            super(callback);
            this.username = username;
            this.password = password;
            this.type = LoginType.KINVEY;
        }

        private Login(String accessToken, LoginType type, KinveyClientCallback callback) {
            super(callback);
            this.accessToken = accessToken;
            this.type = type;
        }

        private Login(String accessToken, String refreshToken, LoginType type, KinveyClientCallback callback) {
            super(callback);
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.type = type;
        }

        private Login(String accessToken, String accessSecret, String consumerKey, String consumerSecret,
                      LoginType type, KinveyClientCallback callback) {
            super(callback);
            this.accessToken = accessToken;
            this.accessSecret = accessSecret;
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
            this.type=type;
        }


        //TODO edwardf method signature is ambiguous with above method if this one also took a login type, so hardcoded to salesforce.
        private Login(String accessToken, String clientID, String refresh, String id, KinveyClientCallback<T> callback){
            super(callback);
            this.accessToken = accessToken;
            this.refreshToken = refresh;
            this.client_id = clientID;
            this.id = id;
            this.type = LoginType.SALESFORCE;
        }

        private Login(Credential credential, KinveyClientCallback callback) {
            super(callback);
            this.credential = credential;
            this.type = LoginType.CREDENTIALSTORE;
        }

        @Override
        protected T executeAsync() throws IOException {
            switch(this.type) {
                case IMPLICIT:
                    return AbstractAsyncUserStore.this.loginBlocking().execute();
                case KINVEY:
                    return AbstractAsyncUserStore.this.loginBlocking(username, password).execute();
                case FACEBOOK:
                    return AbstractAsyncUserStore.this.loginFacebookBlocking(accessToken).execute();
                case GOOGLE:
                    return AbstractAsyncUserStore.this.loginGoogleBlocking(accessToken).execute();
                case TWITTER:
                    return AbstractAsyncUserStore.this.loginTwitterBlocking(accessToken, accessSecret, consumerKey, consumerSecret).execute();
                case LINKED_IN:
                    return AbstractAsyncUserStore.this.loginLinkedInBlocking(accessToken, accessSecret, consumerKey, consumerSecret).execute();
                case AUTH_LINK:
                    return AbstractAsyncUserStore.this.loginAuthLinkBlocking(accessToken, refreshToken).execute();
                case SALESFORCE:
                    return AbstractAsyncUserStore.this.loginSalesForceBlocking(accessToken, client_id, refreshToken, id).execute();
                case MOBILE_IDENTITY:
                    return AbstractAsyncUserStore.this.loginMobileIdentityBlocking(accessToken).execute();
                case CREDENTIALSTORE:
                    return AbstractAsyncUserStore.this.login(credential).execute();
            }
            return null;
        }
    }

    private class Create extends AsyncClientRequest<T> {
        String username;
        String password;

        private Create(String username, String password, KinveyClientCallback<T> callback) {
            super(callback);
            this.username=username;
            this.password=password;

        }

        @Override
        protected T executeAsync() throws IOException {
            return AbstractAsyncUserStore.this.createBlocking(username, password).execute();
        }
    }

    private class Delete extends AsyncClientRequest<Void> {
        boolean hardDelete;

        private Delete(boolean hardDelete, KinveyUserDeleteCallback callback) {
            super(callback);
            this.hardDelete = hardDelete;
        }

        @Override
        protected Void executeAsync() throws IOException {
            AbstractAsyncUserStore.this.deleteBlocking(hardDelete).execute();
            return null;
        }
    }
    
    private class PostForAccessToken extends AsyncClientRequest<T>{
    	
    	private String token;

		public PostForAccessToken(String token, KinveyClientCallback<T> callback) {
			super(callback);
			this.token = token;
		}

		@Override
		protected T executeAsync() throws IOException {
			GenericJson result = AbstractAsyncUserStore.this.getMICToken(token).execute();
			
			T ret =  AbstractAsyncUserStore.this.loginMobileIdentityBlocking(result.get("access_token").toString()).execute();
			
			Credential currentCred = AbstractAsyncUserStore.this.getClient().getStore().load(AbstractAsyncUserStore.this.getCurrentUser().getId());
			currentCred.setRefreshToken(result.get("refresh_token").toString());
			AbstractAsyncUserStore.this.getClient().getStore().store(AbstractAsyncUserStore.this.getCurrentUser().getId(), currentCred);
			
			return ret;
		}
    }
    
    
    private class PostForTempURL extends AsyncClientRequest<T>{
    	
    	
        String username;
        String password;
        
		public PostForTempURL(String username, String password, KinveyUserCallback<T> callback) {
			super(callback);
            this.username=username;
            this.password=password;
		}

		@Override
		protected T executeAsync() throws IOException {
			
			GenericJson tempResult = getMICTempURL().execute();
			String tempURL = tempResult.get("temp_login_uri").toString();
			GenericJson accessResult = AbstractAsyncUserStore.this.MICLoginToTempURL(username, password, tempURL).execute();
		
//			AbstractAsyncUser.this.loginMobileIdentity(accessResult.get("access_token").toString(), MICCallback);
			User user = AbstractAsyncUserStore.this.loginMobileIdentityBlocking(accessResult.get("access_token").toString()).execute();
			
			
			Credential currentCred = AbstractAsyncUserStore.this.getClient().getStore().load(AbstractAsyncUserStore.this.getCurrentUser().getId());
			currentCred.setRefreshToken(accessResult.get("refresh_token").toString());
			AbstractAsyncUserStore.this.getClient().getStore().store(AbstractAsyncUserStore.this.getCurrentUser().getId(), currentCred);
			
			return (T) user;  
		}
    }
    
    

    private class Retrieve<T> extends AsyncClientRequest<T> {

        private Query query = null;
        private String[] resolves = null;

        private Retrieve(KinveyClientCallback callback) {
            super(callback);
        }

        private Retrieve(Query query, KinveyClientCallback callback){
            super(callback);
            this.query = query;
        }

        private Retrieve(String[] resolves, KinveyClientCallback callback){
            super(callback);
            this.resolves = resolves;
        }

        private Retrieve(Query query, String[] resolves, KinveyClientCallback callback){
            super(callback);
            this.query = query;
            this.resolves = resolves;
        }

        @Override
        public T executeAsync() throws IOException {
            if (query == null){
                if (resolves == null){
                    return (T) AbstractAsyncUserStore.this.retrieveBlocking().execute();
                }else{
                    return (T) AbstractAsyncUserStore.this.retrieveBlocking(resolves).execute();
                }
            }else{
                if (resolves == null){
                    return (T) AbstractAsyncUserStore.this.retrieveBlocking(query).execute();
                }else{
                    return (T) AbstractAsyncUserStore.this.retrieveBlocking(query, resolves).execute();
                }

            }

        }
    }


}