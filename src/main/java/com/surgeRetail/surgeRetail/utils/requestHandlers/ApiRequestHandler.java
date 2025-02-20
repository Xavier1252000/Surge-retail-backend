package com.surgeRetail.surgeRetail.utils.requestHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.BigDecimalParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import java.math.BigDecimal;
import java.util.*;

@Data
public class ApiRequestHandler {

    private Map<String, Object>  data= new HashMap<>();

//   1
    public Object getObjectValue(String key){
        if (data==null || StringUtils.isEmpty(key))
            return null;

        return data.get(key)==null?null:data.get(key);
    }

//    2.
    public <T> T getGenericObjectValue(String key, Class<T> clazz){
        if (StringUtils.isEmpty(key) || data == null){
            System.out.println("getObjectValue, either key is empty or object value is null");
            return null;
        }
        Object value = data.get(key);
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
        return String.valueOf(data.get(key));
    }

//    4.
    public Float getFloatValue(String key){
        if(data==null || StringUtils.isEmpty(key))
            return null;

        Object value = data.get(key);

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
        if (data==null || StringUtils.isEmpty(key))
            return null;
        BigDecimal bigDecimal = null;
        Object value = data.get("key");
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
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getDoubleValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = data.get(key);

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
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getIntValue, requestBody with provided field is empty or null");
            return null;
        }
        Object value = data.get(key);

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
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getLongValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = data.get(key);

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
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getShortValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = data.get(key);

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
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getByteValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = data.get(key);

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
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getBooleanValue, either requestBody or method param is empty or null");
            return null;
        }
        Object value = data.get(key);

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
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getListValue, either map or key is empty or null");
            return null;
        }

        Object value = data.get(key);
        if (value == null)
            return null;

        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(value), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//    13.
    public <T>Set<T> getSetValue(String key, Class<T> clazz){
        if (data == null || StringUtils.isEmpty(key)){
            System.out.println("getListValue, either map or key is empty or null");
            return null;
        }

        Object value = data.get(key);
        if (value == null)
            return null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(value), objectMapper.getTypeFactory().constructCollectionType(Set.class, clazz));
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


//    14.
    public Map<?, ?> getMapValue(String key){
        if (data == null && StringUtils.isEmpty(key))
            return null;

        Object value = data.get(key);
        if (value instanceof Map<?,?>)
            return (Map<?, ?>) value;

        return null;
    }
}
