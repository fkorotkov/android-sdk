package com.kinvey.bookshelf;

import android.support.multidex.MultiDexApplication;

import com.kinvey.android.Client;

public class App extends MultiDexApplication {

    private Client sharedClient;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedClient = new Client.Builder(this).build();
    }

    public Client getSharedClient(){
        return sharedClient;
    }
}
