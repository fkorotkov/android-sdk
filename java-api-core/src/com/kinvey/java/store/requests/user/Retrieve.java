package com.kinvey.java.store.requests.user;

import com.google.api.client.util.Key;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.kinvey.java.Query;
import com.kinvey.java.core.AbstractKinveyJsonClientRequest;
import com.kinvey.java.dto.User;
import com.kinvey.java.store.UserStore;

/**
 * Retrieve Request Class, extends AbstractKinveyJsonClientRequest<User>.  Constructs the HTTP request object for
 * Retrieve User requests.
 */
public final class Retrieve<T extends User> extends AbstractKinveyJsonClientRequest<T> {
    private static final String REST_PATH = "user/{appKey}/{userID}{?query,sort,limit,skip,resolve,resolve_depth,retainReference}";

    private UserStore userStore;
    @Key
    private String userID;
    @Key("query")
    private String queryFilter;
    @Key("sort")
    private String sortFilter;
    @Key
    private String limit;
    @Key
    private String skip;

    @Key("resolve")
    private String resolve;
    @Key("resolve_depth")
    private String resolve_depth;
    @Key("retainReferences")
    private String retainReferences;

    public Retrieve(UserStore userStore, String userID, Class<T> myClass) {
        super(userStore.getClient(), "GET", REST_PATH, null, myClass);
        this.userStore = userStore;
        this.userID = userID;
        this.getRequestHeaders().put("X-Kinvey-Client-App-Version", userStore.getClientAppVersion());
        if (userStore.getCustomRequestProperties() != null && !userStore.getCustomRequestProperties().isEmpty()){
            this.getRequestHeaders().put("X-Kinvey-Custom-Request-Properties", new Gson().toJson(userStore.getCustomRequestProperties()) );
        }
    }

    public Retrieve(UserStore userStore, Query query, Class<T> myClass){
        super(userStore.getClient(), "GET", REST_PATH, null, myClass);
        this.userStore = userStore;
        this.queryFilter = query.getQueryFilterJson(userStore.getClient().getJsonFactory());
        int queryLimit = query.getLimit();
        int querySkip = query.getSkip();
        this.limit = queryLimit > 0 ? Integer.toString(queryLimit) : null;
        this.skip = querySkip > 0 ? Integer.toString(querySkip) : null;
        this.sortFilter = query.getSortString();
        this.getRequestHeaders().put("X-Kinvey-Client-App-Version", userStore.getClientAppVersion());
        if (userStore.getCustomRequestProperties() != null && !userStore.getCustomRequestProperties().isEmpty()){
            this.getRequestHeaders().put("X-Kinvey-Custom-Request-Properties", new Gson()
                    .toJson(userStore.getCustomRequestProperties()) );
        }
    }

    public Retrieve(UserStore userStore, String userID, String[] resolve, int resolve_depth, boolean retain, Class<T> myClass){
        super(userStore.getClient(), "GET", REST_PATH, null, myClass);
        this.userStore = userStore;
        this.userID = userID;

        this.resolve = Joiner.on(",").join(resolve);
        this.resolve_depth = resolve_depth > 0 ? Integer.toString(resolve_depth) : null;
        this.retainReferences = Boolean.toString(retain);
        this.getRequestHeaders().put("X-Kinvey-Client-App-Version", userStore.getClientAppVersion());
        if (userStore.getCustomRequestProperties() != null && !userStore.getCustomRequestProperties().isEmpty()){
            this.getRequestHeaders().put("X-Kinvey-Custom-Request-Properties", new Gson()
                    .toJson(userStore.getCustomRequestProperties()) );
        }
    }

    public Retrieve(UserStore userStore, Query query, String[] resolve, int resolve_depth, boolean retain, Class<T> myClass){
        super(userStore.getClient(), "GET", REST_PATH, null, myClass);
        this.userStore = userStore;
        this.queryFilter = query.getQueryFilterJson(userStore.getClient().getJsonFactory());
        int queryLimit = query.getLimit();
        int querySkip = query.getSkip();
        this.limit = queryLimit > 0 ? Integer.toString(queryLimit) : null;
        this.skip = querySkip > 0 ? Integer.toString(querySkip) : null;
        this.sortFilter = query.getSortString();

        this.resolve = Joiner.on(",").join(resolve);
        this.resolve_depth = resolve_depth > 0 ? Integer.toString(resolve_depth) : null;
        this.retainReferences = Boolean.toString(retain);
        this.getRequestHeaders().put("X-Kinvey-Client-App-Version", userStore.getClientAppVersion());
        if (userStore.getCustomRequestProperties() != null && !userStore.getCustomRequestProperties().isEmpty()){
            this.getRequestHeaders().put("X-Kinvey-Custom-Request-Properties", new Gson()
                    .toJson(userStore.getCustomRequestProperties()) );
        }

    }
}
