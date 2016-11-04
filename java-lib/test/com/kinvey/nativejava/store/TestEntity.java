package com.kinvey.nativejava.store;


import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class TestEntity extends GenericJson {
    public static final String COLLECTION = "JLIB";

    @Key("name")
    private String name;

    @Key("_id")
    private String id;

    public TestEntity(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
