package com.surgeRetail.surgeRetail.utils.requestHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.BigDecimalParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.Data;

import javax.sound.midi.Soundbank;
import java.math.BigDecimal;
import java.util.*;

@Data
public class ApiRequestHandler {

    private Map<String, Object>  map= new HashMap<>();

//   1
    public Object getObjectValue(String key){
        if (map==null || StringUtils.isEmpty(key))
            return null;

        return map.get(key)==null?null:map.get(key);
    }

//    2.
    public <T> T getGenericObjectValue(String key, Class<T> clazz){
        if (StringUtils.isEmpty(key) || map == null){
            System.out.println("getObjectValue, either key is empty or object value is null");
            return null;
        }
        Object value = map.get(key);
        if (value == null){
            return null;
        }

        if (clazz.isInstance(value))
            return clazz.cast(value);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
             return objectMapper.convertValue(value, clazz);
        } catch (IllegalArgumentException e) {
            return null;
        }

    }

//    3.
    public String getStringValue(String key){
        if (StringUtils.isEmpty(key))
            return null;
        return String.valueOf(map.get(key));
    }

//    4.
    public Float getFloatValue(String key){
        if(map==null || StringUtils.isEmpty(key))
            return null;

        Object value = map.get(key);

        try {
            if (value instanceof Float) {
                return (Float) value;
            } else if (value instanceof Double) {
                return ((Double) value).floatValue();
            } else if (value instanceof Integer) {
                return ((Integer) value).floatValue();
            } else if (value instanceof BigDecimal) {
                return ((BigDecimal) value).floatValue();
            } else if (value instanceof String) {
                return Float.parseFloat((String) value);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

//    5.
    public BigDecimal getBigDecimalValue(String key){
        if (map==null || StringUtils.isEmpty(key))
            return null;
        BigDecimal bigDecimal = null;
        Object value = map.get("key");
        if (value!=null) {
            if (value instanceof BigDecimal)
                return (BigDecimal) value;

            try {
                bigDecimal = BigDecimalParser.parse((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
            return bigDecimal;
        }
        return null;
    }

//    6.
    public Double getDoubleValue(String key){
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getDoubleValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = map.get(key);

        if (value == null)
            return null;

        if (value instanceof Double)
            return (Double) value;

        try {
            return Double.parseDouble(value.toString());
        }catch (NumberFormatException e){
            return null;
        }
    }

//    7.
    public Integer getIntegerValue(String key){
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getIntValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = map.get(key);

        if (value == null)
            return null;

        if (value instanceof Integer)
            return (Integer) value;

        try {
            return Integer.parseInt(value.toString());
        }catch (NumberFormatException e){
            return null;
        }
    }

//    8.
    public Long getLongValue(String key){
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getLongValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = map.get(key);

        if (value == null)
            return null;

        if (value instanceof Long)
            return (Long) value;

        try {
            return Long.parseLong(value.toString());
        }catch (NumberFormatException e){
            return null;
        }
    }

//    9.
    public Short getShortValue(String key){
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getShortValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = map.get(key);

        if (value == null)
            return null;

        if (value instanceof Short)
            return (Short) value;

        try {
            return Short.parseShort(value.toString());
        }catch (NumberFormatException e){
            return null;
        }
    }

//    10.
    public Byte getByteValue(String key){
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getByteValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = map.get(key);

        if (value == null)
            return null;

        if (value instanceof Byte)
            return (Byte) value;

        try {
            return Byte.parseByte(value.toString());
        }catch (NumberFormatException e){
            return null;
        }
    }

//    11.
    public Boolean getBooleanValue(String key){
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getBooleanValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = map.get(key);

        if (value == null)
            return null;

        if (value instanceof Boolean)
            return (Boolean) value;

        try {
            return Boolean.valueOf(value.toString());
        }catch (ClassCastException e){
            return null;
        }
    }

//    12.
    public <T> List<T> getListValue(String key, Class<T> clazz){
        ObjectMapper objectMapper = new ObjectMapper();
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getListValue, either map or key is empty or null");
            return null;
        }

        Object value = map.get(key);
        if (value == null)
            return null;

//        if (value instanceof List<?>)
//            return (List<T>) value;

        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(value), new TypeReference<List<T>>() {});
        } catch (Exception e) {
            System.out.println("Error parsing list: " + e.getMessage());
            return null;
        }
//        try {
//            return objectMapper.readValue(objectMapper.writeValueAsString(value), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
    }

//    13.
    public <T>Set<T> getSetValue(String key, Class<T> clazz){
        if (map == null || StringUtils.isEmpty(key)){
            System.out.println("getListValue, either map or key is empty or null");
            return null;
        }

        Object value = map.get(key);
        if (value == null)
            return null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(value), new TypeReference<Set<T>>() {});
        } catch (Exception e) {
            System.out.println("Error parsing Set: " + e.getMessage());
            return null;
        }
    }


//    14.
    public Map<?, ?> getMapValue(String key){
        if (map == null && StringUtils.isEmpty(key))
            return null;

        Object value = map.get(key);
        if (value instanceof Map<?,?>)
            return (Map<?, ?>) value;

        return null;
    }
}
