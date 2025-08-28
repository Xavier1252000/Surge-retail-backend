package com.surgeRetail.surgeRetail.utils.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

@Repository
public class GenericFilterService {
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public GenericFilterService(MongoTemplate mongoTemplate,
                                ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> List<T> getObjectByFieldFilters(Class<T> clazz, Map<String, Object> filters) {
        Query query = new Query();

        List<Criteria> criteriaList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (StringUtils.isEmpty(key) || value == null) continue;
            // Extract fieldName and operator
            String[] parts = key.split("_", 3); // allow up to 3 for regex with flags
            if (parts.length < 2) continue; // skip malformed keys

            String fieldName = parts[0];
            String operator = parts[1];
            String flag = parts.length == 3 ? parts[2] : null;

            Set<?> iterableValues = new HashSet<>();
            if (value instanceof Iterable<?>){
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    boolean b = Iterable.class.isAssignableFrom(field.getType()) && !String.class.isAssignableFrom(field.getType());
                    Type genericType = field.getGenericType();
                    Class<?> elementType;
                    if (genericType instanceof ParameterizedType && b){
                        ParameterizedType pt = (ParameterizedType) genericType;
                        Type typeArg = pt.getActualTypeArguments()[0];

                        if (typeArg instanceof Class<?>){
                            elementType = (Class<?>) typeArg;
                            iterableValues = objectMapper.readValue(objectMapper.writeValueAsString(value),
                                    objectMapper.getTypeFactory().constructCollectionType(Set.class, elementType));

                        }
                    }else {
                        if (genericType instanceof Class<?>){
                            elementType = (Class<?>) genericType;
                            iterableValues = objectMapper.readValue(objectMapper.writeValueAsString(value),
                                    objectMapper.getTypeFactory().constructCollectionType(Set.class, elementType));
                        }
                    }
                    } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (value instanceof Iterable<?> && CollectionUtils.isEmpty(iterableValues)) continue;
            switch (operator.toLowerCase()) {
                case "is":
                    criteriaList.add(Criteria.where(fieldName).is(value));
                    break;
                case "in":
                    if (value instanceof Iterable<?>) {
                        criteriaList.add(Criteria.where(fieldName).in(iterableValues));
                    }
                    break;
                case "nin":
                    if (value instanceof Iterable<?>) {
                        criteriaList.add(Criteria.where(fieldName).nin(iterableValues));
                    }
                    break;
                case "ne":
                    criteriaList.add(Criteria.where(fieldName).ne(value));
                    break;
                case "gt":
                    criteriaList.add(Criteria.where(fieldName).gt(value));
                    break;
                case "lt":
                    criteriaList.add(Criteria.where(fieldName).lt(value));
                    break;
                case "gte":
                    criteriaList.add(Criteria.where(fieldName).gte(value));
                    break;
                case "lte":
                    criteriaList.add(Criteria.where(fieldName).lte(value));
                    break;
                case "regex":
                    if (value instanceof String) {
                        if (flag != null) {
                            criteriaList.add(Criteria.where(fieldName).regex((String) value, flag));
                        } else {
                            criteriaList.add(Criteria.where(fieldName).regex((String) value));
                        }
                    }
                    break;
                case "exists":
                    if (value instanceof Boolean) {
                        criteriaList.add(Criteria.where(fieldName).exists((Boolean) value));
                    }
                    break;
                case "all":
                    if (value instanceof Collection<?>) {
                        criteriaList.add(Criteria.where(fieldName).all(iterableValues));
                    }
                    break;
                case "size":
                    if (value instanceof Integer) {
                        criteriaList.add(Criteria.where(fieldName).size((Integer) value));
                    }
                    break;
                case "elemmatch":
                    if (value instanceof Criteria) {
                        criteriaList.add(Criteria.where(fieldName).elemMatch((Criteria) value));
                    }
                    break;
                default:
                    // Unknown operator, optionally log or ignore
                    break;
            }
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        return mongoTemplate.find(query, clazz);
    }
}
