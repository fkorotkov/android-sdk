package com.kinvey.nativejava;


import com.kinvey.java.auth.ClientUser;

public class JavaUserStore implements ClientUser {
    private String userID;
    private static JavaUserStore _instance;

    private JavaUserStore() {
    }

    static JavaUserStore getUserStore() {
        if (_instance == null) {
            _instance = new JavaUserStore();
        }
        return _instance;
    }

    /** {@inheritDoc} */
    @Override
    public void setUser(String userID) {
        this.userID = userID;
    }

    /** {@inheritDoc} */
    @Override
    public String getUser() {
        return userID;
    }

    @Override
    public void clear() {
        userID = null;
    }

}