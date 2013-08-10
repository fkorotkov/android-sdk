/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
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
package com.kinvey.android.push;



import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

/**
 * IntentService responsible for handling GCM messages.
 * <p>
 * Upon successful registration/unregistration with GCM, this class will perform the appropriate action with Kinvey as well.
 * </p>
 * <p>
 * To use GCM for push notifications, extend this class and implement the provided abstract methods.  When GCM related events occur, they relevant method will be called by the library.
 * <p>
 *
 * @author edwardf
 * @since 2.0
 */
public abstract class KinveyGCMService extends GCMBaseIntentService {

    public static final String MESSAGE_FROM_GCM = "msg";

    private Client client;


    /**
     * Public Constructor used by the operating system when an intent is fired.
     */
    public KinveyGCMService() {
        super("GCM PUSH");
    }


    @Override
    protected void onRegistered(Context context, final String registrationId) {
        Log.v(TAG, "Device registered: regId = " + registrationId);


        Client.Builder builder;
        if (getAppKey() == null){
            builder = new Client.Builder(context);
        }else{
            builder = new Client.Builder(getAppKey(), getAppSecret(), context);
        }

        if (gcmEnabled()){
            builder.setSenderIDs(getSenderIDs());
            builder.setGcmInProduction(inProduction());
            builder.enableGCM(gcmEnabled());
        }
        if (getBaseURL() != null){
            builder.setBaseUrl(getBaseURL());
        }

        builder.setRetrieveUserCallback(new KinveyUserCallback() {
            @Override
            public void onSuccess(User result) {
                registerWithKinvey(registrationId, true);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "GCM registration failed to retrieve user!");
            }
        });

        client = builder.build();



    }

    public String getSenderIDs(){
        return null;
    }

    public String getAppKey(){
        return null;
    }

    public String getAppSecret(){
        return null;
    }

    public boolean gcmEnabled(){
        return false;
    }

    public boolean inProduction(){
        return true;
    }

    public String getBaseURL(){
        return null;
    }




    @Override
    protected void onUnregistered(Context context, final String registrationId) {
        Log.v(TAG, "Device unregistered");

        Client.Builder builder;
        if (getAppKey() == null){
            builder = new Client.Builder(context);
        }else{
            builder = new Client.Builder(getAppKey(), getAppSecret(), context);
        }

        if (gcmEnabled()){
            builder.setSenderIDs(getSenderIDs());
            builder.setGcmInProduction(inProduction());
            builder.enableGCM(gcmEnabled());
        }
        if (getBaseURL() != null){
            builder.setBaseUrl(getBaseURL());
        }

        builder.setRetrieveUserCallback(new KinveyUserCallback() {
            @Override
            public void onSuccess(User result) {
                registerWithKinvey(registrationId, false);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "GCM registration failed to retrieve user!");
            }
        });

        client = builder.build();

    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getStringExtra(MESSAGE_FROM_GCM);
        Log.v(TAG, "Received message -> " + message);
        onMessage(message);

    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.v(TAG, "Received deleted messages notification");
        onDelete(total);

    }

    @Override
    public void onError(Context context, String errorId) {
        Log.v(TAG, "Received error: " + errorId);
        onError(errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.v(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }



    private void registerWithKinvey(String gcmID, boolean register){
        registerWithKinvey(client, gcmID, register);
    }

    private void registerWithKinvey(Client client, final String gcmRegID, boolean register) {
        //registered on GCM but not on Kinvey?
        Log.v(Client.TAG , "about to register with Kinvey");
        if (client == null){
            Log.e(Client.TAG, "GCMService got garbage collected, cannot complete registration!");
            return;
        }

        if (!client.user().isUserLoggedIn()) {
            Log.e(Client.TAG, "Need to login a current user before registering for push!");
            return;
        }

        if (register) {
            //send registration to Kinvey
            GCMPush.PushConfig config = new GCMPush.PushConfig();
            GCMPush.PushConfigField active = new GCMPush.PushConfigField();
            //TODO -- don't just set IDs, get it, add new one, and then save it (multiple devices -> multiple GCM ids)
            active.setIds(new String[]{gcmRegID});
            //active.setNotificationKey(gcmRegID);
            if (!client.push().isInProduction()) {
                config.setGcmDev(active);
            } else {
                config.setGcm(active);
            }
            client.user().put("_push", config);
        } else {
            //remove push from user object
            client.user().remove("_push");
            client.user().put("_push", null);
        }
        client.user().update(new KinveyUserCallback() {
            @Override
            public void onSuccess(User result) {
                Log.v(Client.TAG , "GCM - user updated successfully, push exists? -> " + result.containsKey("_push"));
                if (result.containsKey("_push")){
                    KinveyGCMService.this.onRegistered(gcmRegID);

                }else{
                    KinveyGCMService.this.onUnregistered(gcmRegID);

                }


            }

            @Override
            public void onFailure(Throwable error) {
                Log.v(Client.TAG , "GCM - user update error: " + error);
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        GCMRegistrar.onDestroy(getApplicationContext());
    }


    /**
     * This method is called when a message is received through GCM via Kinvey.
     * <p/>
     * the String message is exactly as it is sent from the Console, so it can be displayed immediately with a NotificationBuilder, or can be parsed to perform actions.
     *
     * @param message the text of the message
     */
    public abstract void onMessage(String message);


    /**
     * This method is called when an error occurs with GCM.
     *
     * @param error the text of the error message
     */
    public abstract void onError(String error);

    /**
     * This method is called when GCM messages are deleted.
     *
     * @param deleteCount the number of deleted messages
     */
    public abstract void onDelete(int deleteCount);

    /**
     * This method is executed after `myKinveyClient.push().initialize(getApplication());` has been called and registration has completed.
     * This can use this pop a notification using a `NotificationBuilder`, or just log output.
     * <p/>
     * This method will register the current user with Kinvey, after successfully registering with GCM.
     *
     * @param gcmID the new user's unique GCM registration ID
     */
    public abstract void onRegistered(String gcmID);

    /**
     * This method is called after successful unregistration.  This includes removing push from both GCM as well as Kinvey.
     *
     * @param oldID the old GCM registration ID of the now unregistered user.
     */
    public abstract void onUnregistered(String oldID) ;




}
