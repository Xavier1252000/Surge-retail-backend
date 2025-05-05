package com.surgeRetail.surgeRetail.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surgeRetail.surgeRetail.document.store.OperatingHours;
import com.surgeRetail.surgeRetail.document.store.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.ClientDetails;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.repository.ClientDeskApiRepository;
import com.surgeRetail.surgeRetail.repository.ConfidentialApiRepository;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.utils.AppUtils;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class ClientDeskApiService {

    private final ClientDeskApiRepository cdRepository;
    private final PublicApiRepository publicApiRepository;
    private final ObjectMapper objectMapper;
    private final ConfidentialApiRepository confidentialApiRepository;

    public ClientDeskApiService(ClientDeskApiRepository cdRepository,
                                PublicApiRepository publicApiRepository,
                                ObjectMapper objectMapper,
                                ConfidentialApiRepository confidentialApiRepository){
        this.cdRepository = cdRepository;
        this.publicApiRepository = publicApiRepository;
        this.objectMapper = objectMapper;
        this.confidentialApiRepository = confidentialApiRepository;
    }


    public ApiResponseHandler addStore(String clientId, String storeType, String storeName, String contactNo, String registrationNo, String taxIdentificationId, String taxIdentificationNo, String address, String city, String state, String country, Set<String> staffIds, String pinCode, String email, String timezone, Set<OperatingHours> operatingHours, String currency) {

        objectMapper.registerModule(new JavaTimeModule());

//      in case clientId is externally provided by super-admin, not taken from SecurityContextHolder when client is creating store
        ClientDetails cd = cdRepository.findClientByClientId(clientId);
        if (cd == null)
            return new ApiResponseHandler("no client found by provided clientId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Store store = new Store();
        store.setClientId(clientId);
        store.setStoreType(storeType);
        store.setStoreName(storeName);
        store.setContactNo(contactNo);
        store.setRegistrationNo(registrationNo);
        store.setTaxIdentificationId(taxIdentificationId);
        store.setTaxIdentificationNo(taxIdentificationNo);
        store.setEmail(email);
        store.setTimezone(timezone);
        store.setOperatingHours(operatingHours);
        store.setAddress(address);
        store.setCity(city);
        store.setPostalCode(pinCode);
        store.setState(state);
        store.setCountry(country);
        store.setStaffIds(staffIds);
        store.setCurrency(currency);
        store.onCreate();

        Store savedStore = cdRepository.saveStore(store);
        ObjectNode node  = objectMapper.createObjectNode();
        node.put("id", savedStore.getId());
        node.put("clientId", savedStore.getClientId());
        node.put("store_Name", savedStore.getStoreName());
        node.put("storeContactNo", savedStore.getContactNo());
        node.put("registrationNo", savedStore.getRegistrationNo());
        node.put("taxIdentificationId", savedStore.getTaxIdentificationId());
        node.put("taxIdentificationNo",taxIdentificationNo);
        node.put("pinCode", store.getPostalCode());
        node.put("city", savedStore.getCity());
        node.put("state", savedStore.getState());
        node.set("operating_hours", objectMapper.valueToTree(savedStore.getOperatingHours()));
        node.put("timezone", savedStore.getTimezone());
        node.put("country", savedStore.getCountry());
        node.put("createdOn", String.valueOf(savedStore.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(savedStore.getModifiedOn()));
        node.put("createdBy",String.valueOf(savedStore.getCreatedBy()));
        node.put("updatedBy",savedStore.getModifiedBy());
        node.put("active",savedStore.getActive());
        return new ApiResponseHandler("store created successfully!!!", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler findStoreByStaffId(String staffId) {
        List<Store> storeByStaffId = cdRepository.findStoreByStoreAdminId(staffId);
        if (storeByStaffId==null)
            return new ApiResponseHandler("No store found for staff", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        return new ApiResponseHandler("Store fetched successfully", storeByStaffId, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }
}
