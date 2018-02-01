package com.kinvey.bookshelf;


import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class HierarchyCache extends GenericJson {
    public static final String COLLECTION = "hierarchycache";

    public HierarchyCache() {
    }

    @Key("_id")
    String id;

    @Key("SalesOrganization")
    String salesOrganization;

    @Key("DistributionChannel")
    String distributionChannel;

    @Key("SAPCustomerNumber")
    String sapCustomerNumber;

    @Key("MaterialNumber")
    String materialNumber;

    @Key("ConditionType")
    String conditionType;

    @Key("SalesDivision")
    String salesDivision;

    @Key("ValidityStartDate")
    String validityStartDate;

    @Key("ValidityEndDate")
    String validityEndDate;

    @Key("Price")
    String price;

    @Key("Currency")
    String currency;

    @Key("DeliveryUnit")
    String deliverUnit;

    @Key("UnitQuantity")
    String unitQuantity;

    @Key("UnitOfMeasure")
    String unitOfMeasure;
}