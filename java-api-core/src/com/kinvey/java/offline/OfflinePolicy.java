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
package com.kinvey.java.offline;


import java.io.IOException;

/**
 * This enum set determines behaivor of an Offline Request
 *
 *
 * @author edwardf
 */
public enum OfflinePolicy {


    ALWAYS_ONLINE{
        @Override
        public <T> T execute(AbstractKinveyOfflineClientRequest<T> offlineRequest) throws IOException {
            return offlineRequest.offlineFromService(true);
        }
    },
    ONLINE_FIRST{
        @Override
        public <T> T execute(AbstractKinveyOfflineClientRequest<T> offlineRequest) throws IOException {
            T ret = offlineRequest.offlineFromService(false);
            if (ret == null){
                ret = offlineRequest.offlineFromStore();
            }
            return ret;
        }
    },
    LOCAL_FIRST{
        @Override
        public <T> T execute(AbstractKinveyOfflineClientRequest<T> offlineRequest) throws IOException {
            T ret =  offlineRequest.offlineFromStore();
            System.out.println("*** local first-> " + ret);
            if (ret == null){
                ret = offlineRequest.offlineFromService(false);
                System.out.println("*** local first online-> " + ret);
            }
            return ret;
        }
    };















    public abstract <T> T execute(AbstractKinveyOfflineClientRequest<T> offlineRequest) throws IOException;



    }
