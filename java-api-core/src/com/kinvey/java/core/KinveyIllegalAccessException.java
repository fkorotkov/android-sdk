package com.kinvey.java.core;


import com.kinvey.java.KinveyException;

public class KinveyIllegalAccessException extends KinveyException {


    public KinveyIllegalAccessException(String reason, String fix, String explanation) {
        super(reason, fix, explanation);
    }

    public KinveyIllegalAccessException(String reason) {
        super(reason);
    }

}
