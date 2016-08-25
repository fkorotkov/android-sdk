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

package com.kinvey.java.store.requests.user;

import com.google.api.client.http.HttpContent;
import com.google.api.client.json.GenericJson;
import com.kinvey.java.core.AbstractKinveyClientRequest;
import com.kinvey.java.dto.User;
import com.kinvey.java.store.UserStore;

/**
 * Created by Prots on 2/12/16.
 */
public final class GetMICTempURL<T extends User> extends AbstractKinveyClientRequest<GenericJson> {
    private static final String REST_PATH = "oauth/auth";

    private UserStore<T> userStore;

    public GetMICTempURL(UserStore<T> userStore, HttpContent content) {
        super(userStore.getClient(), userStore.MICHostName, "POST", REST_PATH, content, GenericJson.class);
        this.userStore = userStore;
        if (userStore.MICApiVersion != null && userStore.MICApiVersion.length() > 0) {
            this.uriTemplate = userStore.MICApiVersion + "/" + this.uriTemplate;
        }
    }
}