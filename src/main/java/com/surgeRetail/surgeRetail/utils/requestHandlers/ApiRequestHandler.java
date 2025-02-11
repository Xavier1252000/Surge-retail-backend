package com.surgeRetail.surgeRetail.utils.requestHandlers;

import io.micrometer.common.util.StringUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class ApiRequestHandler {

    private Map<String, Object>  map= new HashMap<>();

    public String getStringValue(String key){
        if (StringUtils.isEmpty(key))
            return null;
        return String.valueOf(map.get(key));
    }

    public Integer getIntegerValue(String key){
        if (StringUtils.isEmpty(key))
            return null;
        try {
            return Integer.valueOf(map.get(key).toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Float getFloatValue(String key){
        if(map==null || key==null || StringUtils.isEmpty(key))
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
}
