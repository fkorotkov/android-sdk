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

package com.kinvey.java.query;

//import com.google.gson.Gson;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kinvey.java.KinveyException;
import com.kinvey.java.query.QueryFilter.QueryFilterBuilder;

/**
 * Abstract class for Query objects.
 *
 * @author mjsalinger
 * @since 2.0
 */
public abstract class AbstractQuery implements Serializable{

    private static final long serialVersionUID = 5635939847038496849L;


    /**
     * Possible sort orders (Ascending and Descending)
     */
    public enum SortOrder {
        ASC,
        DESC
    }

    protected QueryFilterBuilder builder;
    protected LinkedHashMap<String,SortOrder> sort;
    private String queryString = null;

    /**
     * Constructor, sets the appropriate Filter builder and creates a Sort object
     *
     * @param builder
     */
    public AbstractQuery(QueryFilterBuilder builder) {
        this.builder = builder;
        this.sort = new LinkedHashMap<String, SortOrder>();
    }

    
    /**
     * Sets the raw query string
     * 
     * @param queryString
     * @return this
     */
    public AbstractQuery setQueryString(String queryString){
        this.queryString = queryString;
        return this;
    }

    /**
     *
     * @return Query filter as AbstractMap<String, Object>
     */
    public AbstractMap<String,Object> getQueryFilterMap() {
        return builder.getFilterMap();
    }

    /**
     *
     * @return Query filter as JSon
     */
    public String getQueryFilterJson(JsonFactory factory) {
        Preconditions.checkNotNull(factory);
        if (queryString != null){
            return this.queryString;
        }

        StringWriter writer = new StringWriter();
        String jsonResult = "";
        try {
            JsonGenerator generator = factory.createJsonGenerator(writer);
            AbstractMap<String, Object> filterMap = getQueryFilterMap();
            buildQueryString(generator, filterMap);
            generator.flush();
            jsonResult = writer.toString();
        } catch (Exception ex) {
            throw new KinveyException("Exception thrown during building of query.", "Please inspect the QueryMap for errors: " + getQueryFilterMap(), ex.getMessage());
        }

        if (jsonResult.equals("{}") || jsonResult.equals("")) {
            return null;
        }

        return jsonResult;
    }

    private void buildQueryString(JsonGenerator generator, AbstractMap<String, Object> filterMap) throws IOException {
        try {
            int filterMapSize = filterMap.entrySet().size();
            boolean hasMultipleFilters = false;
            int i = 0;

            for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
                if (!hasMultipleFilters) {
                    generator.writeStartObject();
                }
                if (entry.getValue() == null) {
                    generator.writeFieldName(entry.getKey());
                    generator.writeNull();
                }
                else {
                    Class valueClass = entry.getValue().getClass();
                    if (valueClass.equals(String.class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeString((String)entry.getValue());
                    } else if (valueClass.equals(String[].class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeStartArray();
                        String[] valueStr = (String[]) entry.getValue();
                        for (String str : valueStr) {
                            generator.writeString(str);
                        }
                        generator.writeEndArray();
                    } else if (valueClass.equals(Boolean.class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeBoolean((boolean) entry.getValue());
                    } else if (valueClass.equals(Boolean[].class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeStartArray();
                        Boolean[] valueBool = (Boolean[]) entry.getValue();
                        for (boolean bool : valueBool) {
                            generator.writeBoolean(bool);
                        }
                        generator.writeEndArray();
                    } else if (valueClass.equals(Integer.class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeNumber((int)entry.getValue());
                    } else if (valueClass.equals(Integer[].class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeStartArray();
                        Integer[] valueInt = (Integer[]) entry.getValue();
                        for (int integer : valueInt) {
                            generator.writeNumber(integer);
                        }
                        generator.writeEndArray();
                    } else if (valueClass.equals(Long.class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeNumber((long)entry.getValue());
                    } else if (valueClass.equals(Long[].class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeStartArray();
                        Long[] valueLong = (Long[]) entry.getValue();
                        for (long longVal : valueLong) {
                            generator.writeNumber(longVal);
                        }
                        generator.writeEndArray();
                    } else if (valueClass.equals(Double.class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeNumber((double)entry.getValue());
                    } else if (valueClass.equals(Double[].class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeStartArray();
                        Double[] valueDouble = (Double[]) entry.getValue();
                        for (double doubleVal : valueDouble) {
                            generator.writeNumber(doubleVal);
                        }
                        generator.writeEndArray();
                    } else if (valueClass.equals(Float.class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeNumber((float)entry.getValue());
                    } else if (valueClass.equals(Float[].class)) {
                        generator.writeFieldName(entry.getKey());
                        generator.writeStartArray();
                        Float[] valueFloat = (Float[]) entry.getValue();
                        for (float floatVal : valueFloat) {
                            generator.writeNumber(floatVal);
                        }
                        generator.writeEndArray();
                    } else if (valueClass.equals(LinkedHashMap.class)) {
                        // Value is an added filter
                        generator.writeFieldName(entry.getKey());
                        buildQueryString(generator, (LinkedHashMap) entry.getValue());
                    } else if (valueClass.getComponentType() != null &&
                               valueClass.getComponentType().equals(LinkedHashMap.class)) {
                        if (valueClass.isArray()) {
                            // Value is a map, so this is a nested query. Recursively call into it.
                            generator.writeFieldName(entry.getKey());
                            generator.writeStartArray();
                            LinkedHashMap[] valueMap = (LinkedHashMap<String, Object>[]) entry.getValue();
                            for (LinkedHashMap map : valueMap) {
                                buildQueryString(generator, map);
                            }
                            generator.writeEndArray();
                        } else {
                            // Value is an added filter
                            buildQueryString(generator, (LinkedHashMap) entry.getValue());
                        }
                    }
                }

                i++;
                if (filterMapSize > 1 && i < filterMapSize) {
                    // there are multiple filters and we have not reached the last one
                    hasMultipleFilters = true;
                } else {
                    generator.writeEndObject();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Adds a sort  sort condition to the Query
     *
     * @param field Field to sort on
     * @param order Order to sort values (Ascending/Descending)
     * @return  Query object
     */
    public AbstractQuery addSort(String field, SortOrder order) {
        Preconditions.checkNotNull(field);
        if (order == null) {
            order = SortOrder.ASC;
        }
        sort.put(field,order);
        return this;
    }

    public Map<String, SortOrder> getSort(){
        return sort;
    }

    // Abstract Methods
    public abstract AbstractQuery equals(String key, Object value);
    public abstract AbstractQuery greaterThan(String key, Object value);
    public abstract AbstractQuery lessThan(String key, Object value);
    public abstract AbstractQuery greaterThanEqualTo(String key, Object value);
    public abstract AbstractQuery lessThanEqualTo(String key, Object value);
    public abstract AbstractQuery notEqual(String key, Object value);
    public abstract AbstractQuery in(String key, Object[] value);
    public abstract AbstractQuery notIn(String key, Object[] value);
    public abstract AbstractQuery regEx(String key, Object value);
    public abstract AbstractQuery startsWith(String key, Object value);
    //public abstract AbstractQuery endsWith(String key, Object value);
    public abstract AbstractQuery all(String key, Object[] value);
    public abstract AbstractQuery size(String key, int value);
    public abstract AbstractQuery and(AbstractQuery query);
    public abstract AbstractQuery or(AbstractQuery query);
    public abstract AbstractQuery not();
    public abstract String getSortString();
    public abstract AbstractQuery nearSphere(String field, double lat, double lon);
    public abstract AbstractQuery nearSphere(String field, double lat, double lon, double maxDistance);
    public abstract AbstractQuery withinBox(String field, double pointOneLat, double pointOneLon,
                                            double pointTwoLat, double pointTwoLon);
    public abstract AbstractQuery withinPolygon(String field, double pointOneLat, double pointOneLon,
                                                double pointTwoLat, double pointTwoLon,
                                                double pointThreeLat, double pointThreeLon,
                                                double pointFourLat, double pointFourLon);

}
