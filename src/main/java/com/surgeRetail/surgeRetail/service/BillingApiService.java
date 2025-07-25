package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surgeRetail.surgeRetail.document.orderAndInvoice.Invoice;
import com.surgeRetail.surgeRetail.dtos.InvoiceRequestDto;
import com.surgeRetail.surgeRetail.utils.generic.GenericFilterService;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class BillingApiService {

    private final GenericFilterService genericFilterService;
    private final ObjectMapper objectMapper;

    public BillingApiService(GenericFilterService genericFilterService,
                             ObjectMapper objectMapper) {
        this.genericFilterService = genericFilterService;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<ApiResponseHandler> listInvoiceByFilters(InvoiceRequestDto invoiceFilters) {
        objectMapper.registerModule(new JavaTimeModule());

//        Map<String, Object> filterMap;
//        try {
//            filterMap = objectMapper.readValue(objectMapper.writeValueAsString(invoiceFilters), objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        Map<String, Object> filterMap = new HashMap<>();
        Field[] fields = InvoiceRequestDto.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(invoiceFilters);
                if (value != null) {
                    filterMap.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }

        List<Invoice> invoices = genericFilterService.getObjectByFieldFilters(Invoice.class, filterMap);
        return ApiResponseHandler.createResponse("Success", invoices, ResponseStatusCode.SUCCESS);
    }
}
