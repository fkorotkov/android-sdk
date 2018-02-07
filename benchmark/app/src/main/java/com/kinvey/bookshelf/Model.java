package com.kinvey.bookshelf;


import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class Model extends GenericJson{

    public static final String COLLECTION = "modelCollection";

    public Model() {
    }

    @Key
    String firstName;

    @Key
    String secondName;

    @Key
    String thirdName;

    @Key
    String company;

    @Key
    String title;

    @Key
    String department;

    @Key
    String price;

    @Key
    String currency;

    @Key
    String testField1;

    @Key
    String testField2;

    @Key
    String testField3;

    @Key
    String testField4;

    @Key
    String testField5;

    @Key
    String testField6;

    @Key
    String testField7;

    @Key
    String testField8;

    @Key
    String testField9;

    @Key
    String testField10;

    @Key
    Integer age;

    @Key
    Integer year;

}
