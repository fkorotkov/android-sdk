package com.kinvey.java.store;


import com.kinvey.java.AbstractClient;
import com.kinvey.java.Query;
import com.kinvey.java.auth.Credential;
import com.kinvey.java.auth.KinveyAuthRequest;
import com.kinvey.java.core.KinveyClientRequestInitializer;
import com.kinvey.java.dto.User;

import java.io.IOException;

/**
 * Class for static and synchronous call UserStoreRequestManager's methods.
 * Methods of this class are used for user managing.
 *
 */
public abstract class UserStore {

    /**
     * Create new Kinvey user with entered credentials.
     * Synchronous static method.
     *
     * @param userId the id of user
     * @param  password password of user which will be created
     * @param client Kinvey client instance
     * @return User object which is created
     * @throws IOException
    */
    public static User signUp(String userId, String password, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).createBlocking(userId, password).execute();
    }

    /**
     * Delete current Kinvey user.
     * Synchronous static method.
     *
     * @param isHard if true, physically deletes the user. If false, marks user as inactive.
     * @param client Kinvey client instance
     * @throws IOException
     */
    public static  void destroy(boolean isHard, AbstractClient client) throws IOException {
        new UserStoreRequestManager(client, createBuilder(client)).deleteBlocking(isHard).execute();
    }

    /**
     * Login with Kinvey user and password.
     * If user does not exist, returns a error response.
     * Synchronous static method.
     *
     * @param username userID of Kinvey User
     * @param password password of Kinvey user
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static  User login(String username, String password,
                              AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginBlocking(username, password).execute();
    }

    /**
     * Login with the implicit user.  If implicit user does not exist, the user is created.
     * After calling this method, the application should retrieve and store the userID using getId()
     * Synchronous static method.
     *
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static  User login(AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginBlocking().execute();
    }

    /**
     * Login to Kinvey services using Facebook access token obtained through OAuth2.
     * If the user does not exist in the Kinvey service, the user will be created.
     * Synchronous static method.
     *
     * @param accessToken Facebook-generated access token.
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginFacebook(String accessToken, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginFacebookBlocking(accessToken).execute();
    }

    /**
     * Login to Kinvey services using Google access token obtained through OAuth2.
     * If the user does not exist in the Kinvey service, the user will be created.
     * Synchronous static method.
     *
     * @param accessToken Google-generated access token
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginGoogle(String accessToken, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginGoogleBlocking(accessToken).execute();
    }

    /**
     * Login to Kinvey services using Twitter-generated access token, access secret, consumer key, and consumer secret
     * obtained through OAuth1a.  If the user does not exist in the Kinvey service, the user will be created.
     * Synchronous static method.
     *
     * @param accessToken Twitter-generated access token
     * @param accessSecret Twitter-generated access secret
     * @param consumerKey Twitter-generated consumer key
     * @param consumerSecret Twitter-generated consumer secret
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginTwitter(String accessToken, String accessSecret, String consumerKey,
                                    String consumerSecret, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).
                loginTwitterBlocking(accessToken, accessSecret, consumerKey, consumerSecret).execute();
    }

    /**
     * Login to Kinvey services using LinkedIn-generated access token, access secret, consumer key, and consumer secret
     * obtained through OAuth1a.  If the user does not exist in the Kinvey service, the user will be created.
     * Synchronous static method.
     *
     * @param accessToken Linked In generated access token
     * @param accessSecret Linked In generated access secret
     * @param consumerKey Linked In generated consumer key
     * @param consumerSecret Linked In generated consumer secret
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginLinkedIn(String accessToken, String accessSecret, String consumerKey,
                                     String consumerSecret, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginLinkedInBlocking(accessToken, accessSecret, consumerKey, consumerSecret).execute();
    }

    /***
     * Login to Kinvey Services
     * Synchronous static method.
     *
     * @param accessToken
     * @param refreshToken
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginAuthLink(String accessToken, String refreshToken,
                                     AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginAuthLinkBlocking(accessToken, refreshToken).execute();
    }

    /**
     * Login to Kinvey services using SalesForce access token obtained through OAuth2.  If the user does not exist in the
     * Kinvey service, the user will be created.
     * Synchronous static method.
     *
     * @param accessToken SalesForce-generated access token
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginSalesForce(String accessToken, String clientId,
                                       String refreshToken, String id,
                                       AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginSalesForceBlocking(accessToken, clientId, refreshToken, id).execute();
    }

    /***
     * Login to Kinvey Services using Mobile Identity Connect
     * Synchronous static method.
     *
     * @param authToken
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginMobileIdentity(String authToken, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client))
                .loginMobileIdentityBlocking(authToken).execute();
    }

    /**
     * Log in with existing credential
     * Synchronous static method.
     *
     * @param credential
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User login(Credential credential, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).login(credential).execute();
    }

    /**
     * Login to Kinvey services using a Kinvey user's _id and their valid Kinvey Auth Token.  This method is provided
     * to allow for cross-platform login, by reusing a session provided with another client library (or the REST api).
     * Synchronous static method.
     *
     * @param userId the _id field of the user to login
     * @param authToken a valid Kinvey Auth token
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User loginKinveyAuthToken(String userId, String authToken, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).loginKinveyAuthTokenBlocking(userId, authToken).execute();
    }

    /**
     * Logs the user out of the current app
     * Synchronous static method.
     *
     * @param client Kinvey client instance
     * @throws IOException
     */
    public static void logout(AbstractClient client) throws IOException {
        new UserStoreRequestManager(client, createBuilder(client)).logout().execute();
    }

    /**
     * Initiates an EmailConfirmation request for the current user
     * Synchronous static method.
     *
     * @param client Kinvey client instance
     * @throws IOException
     */
    public static void sendEmailConfirmation(AbstractClient client) throws IOException {
        new UserStoreRequestManager(client, createBuilder(client)).sendEmailVerificationBlocking().execute();
    }

    /**
     * Initiates a password reset request for a provided username
     * Synchronous static method.
     *
     * @param usernameOrEmail the username to request a password reset for
     * @param client Kinvey client instance
     * @throws IOException
     */
    public static void resetPassword(String usernameOrEmail, AbstractClient client) throws IOException {
        new UserStoreRequestManager(client, createBuilder(client)).resetPasswordBlocking(usernameOrEmail).execute();
    }

    /**
     * Change password for current user's profile
     * Synchronous static method.
     *
     * @param password the password to update
     * @param client Kinvey client instance
     * @throws IOException
     */
    public static void changePassword(String password, AbstractClient client) throws IOException {
        new UserStoreRequestManager(client, createBuilder(client)).changePassword(password).execute();
    }

    /**
     * Convenience Method to retrieve Metadata.
     * Synchronous static method.
     *
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User convenience(AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).retrieveMetadataBlocking();
    }

    /**
     * Retrieves current user's metadata.
     * Synchronous static method.
     *
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User retrieve(AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).retrieveBlocking().execute();
    }

    /**
     * Retrieves an array of User[] based on a Query.
     * Synchronous static method.
     *
     * @param client Kinvey client instance
     * @return User[] objects
     * @throws IOException
     */
    public static User[] retrieve(Query query, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).retrieveBlocking(query).execute();
    }

    /**
     * Retrieve current user's metadata with support for resolving KinveyReferences
     * Synchronous static method.
     *
     * @param resolves - List of {@link com.kinvey.java.model.KinveyReference} fields to resolve
     * @param client Kinvey client instance
     * @return User object
     * @throws IOException
     */
    public static User retrieve(String[] resolves, AbstractClient client) throws IOException {
        return  new UserStoreRequestManager(client, createBuilder(client)).retrieveBlocking(resolves).execute();
    }

    /**
     * Retrieves an array of User[] based on a Query with support for resolving KinveyReferences
     * Synchronous static method.
     *
     * @param query the query to execute
     * @param resolves - List of {@link com.kinvey.java.model.KinveyReference} fields to resolve
     * @param client Kinvey client instance
     * @return User[] objects
     * @throws IOException
     */
    public static User[] retrieve(Query query, String[] resolves, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).retrieveBlocking(query, resolves).execute();
    }

    /**
     * Forgot username by email
     * Synchronous static method.
     *
     * @param client Kinvey client instance
     * @param email the email for finding user
     * @throws IOException
     */
    public static void forgotUsername(AbstractClient client, String email) throws IOException {
        new UserStoreRequestManager(client, createBuilder(client)).forgotUsername(email).execute();
    }

    /**
     * Check user existing by username
     * Synchronous static method.
     *
     * @param username the userName of Kinvey user
     * @param client Kinvey client instance
     * @return UserExists request ready to execute
     * @throws IOException
     */
    public static boolean exists( String username, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).exists(username).execute();
    }

    /**
     * Get updated user by userId
     *
     * @param userId the userId of Kinvey user
     * @param client Kinvey client instance
     * @return Update request ready to execute
     * @throws IOException
     */
    public static User get(String userId, AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).getUser(userId).execute();
    }

    /**
     * Save current user's data
     *
     * @param client Kinvey client instance
     * @return Update request ready to execute
     * @throws IOException
     */
    public static User save(AbstractClient client) throws IOException {
        return new UserStoreRequestManager(client, createBuilder(client)).save().execute();
    }

    /**
     * Create Builder for UserStoreRequestManager
     *
     * @param client Kinvey client instance
     * @return KinveyAuthRequest.Builder
     * @throws IOException
     */
    private static KinveyAuthRequest.Builder createBuilder(AbstractClient client) {
        String appKey = ((KinveyClientRequestInitializer) client.getKinveyRequestInitializer()).getAppKey();
        String appSecret = ((KinveyClientRequestInitializer) client.getKinveyRequestInitializer()).getAppSecret();

        return new KinveyAuthRequest.Builder(client.getRequestFactory().getTransport(),
                client.getJsonFactory(), client.getBaseUrl(), appKey, appSecret, null);
    }

}
