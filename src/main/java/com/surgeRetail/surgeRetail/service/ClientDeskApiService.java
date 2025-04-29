package com.surgeRetail.surgeRetail.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surgeRetail.surgeRetail.document.store.OperatingHours;
import com.surgeRetail.surgeRetail.document.store.Store;
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



    public ApiResponseHandler registerUserWithCustomRoles(String firstName, String lastName, String emailId, String mobileNo, String username, String password, Set<String>roles, String superAdminSecret, String clientSecret) {

        User customUser = new User();
        customUser.setFirstName(firstName);
        customUser.setLastName(lastName);
        customUser.setUsername(username);
        customUser.setEmailId(emailId);
        customUser.setMobileNo(mobileNo);
        customUser.setPassword(password);
        customUser.setRoles(roles);
        customUser.setCreatedOn(Instant.now());
        customUser.setModifiedOn(Instant.now());
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        customUser.setCreatedBy(principal.getId());
        customUser.setModifiedBy(principal.getId());
        customUser.setActive(true);

        publicApiRepository.save(customUser);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("firstName", customUser.getFirstName());
        node.put("lastName", customUser.getLastName());
        node.put("username", customUser.getUsername());
        node.put("emailId", customUser.getEmailId());
        node.put("mobileNo", customUser.getMobileNo());
        node.put("password", customUser.getPassword()); // Consider encrypting before saving
        node.set("roles", objectMapper.valueToTree(customUser.getRoles()));
        node.put("createdOn", customUser.getCreatedOn().toString());
        node.put("modifiedOn", customUser.getModifiedOn().toString());
        node.put("createdBy", customUser.getCreatedBy());
        node.put("modifiedBy", customUser.getModifiedBy());
        node.put("active", customUser.isActive());
        return new ApiResponseHandler("registered with role"+"  ", null, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }


    public ApiResponseHandler addStore(String clientId, String storeType, String storeName, String contactNo, String registrationNo, String taxIdentificationId, String taxIdentificationNo, String address, String city, String state, String country, Set<String> storeAdminIds, String pinCode, String email, String timezone, Set<OperatingHours> operatingHours, String currency) {

        objectMapper.registerModule(new JavaTimeModule());

//      in case clientId is externally provided by super-admin, not taken from SecurityContextHolder when client is creating store
        User client = publicApiRepository.findUserByUserId(clientId);
        if (client == null)
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
        store.setStoreAdminIds(storeAdminIds);
        store.setCurrency(currency);
        store.onCreate();

        Store savedStore = cdRepository.saveStore(store);
        ObjectNode node  = objectMapper.createObjectNode();
        node.put("id", savedStore.getId());
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

    public ApiResponseHandler findStoreByStoreAdminId(String storeAdminId) {
        Store storeByStoreAdminId = cdRepository.findStoreByStoreAdminId(storeAdminId);
        if (storeByStoreAdminId==null)
            return new ApiResponseHandler("No store found for user", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        ObjectNode node = objectMapper.createObjectNode();
        try {
            node = AppUtils.mapObjectToObjectNode(storeByStoreAdminId);
        } catch (IllegalAccessException e) {
            return new ApiResponseHandler("internal server error", null, ResponseStatus.INTERNAL_SERVER_ERROR, ResponseStatusCode.INTERNAL_SERVER_ERROR, true);
        }
        return new ApiResponseHandler("Store fetched successfully", node, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }
}
