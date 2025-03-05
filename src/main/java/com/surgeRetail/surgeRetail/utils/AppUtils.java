package com.surgeRetail.surgeRetail.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class AppUtils {

    public static ObjectNode mapObjectToObjectNode(Object object) throws IllegalAccessException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                node.put(field.getName(), objectMapper.valueToTree(field.get(object)));
            }
        }
        return node;
    }
}
