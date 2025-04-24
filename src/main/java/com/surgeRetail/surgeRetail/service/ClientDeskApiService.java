package com.surgeRetail.surgeRetail.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.Item.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.repository.ClientDeskApiRepository;
import com.surgeRetail.surgeRetail.repository.ConfidentialApiRepository;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
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


    public ApiResponseHandler addStore(String clientId, String storeName, String storeContactNo, String registrationNo, String gstNo, String city, String state, String country, Set<String> storeAdminIds, String pinCode) {

//      in case clientId is externally provided by super-admin, not taken from SecurityContextHolder when client is creating store
        User client = publicApiRepository.findUserByUserId(clientId);
        if (client == null)
            return new ApiResponseHandler("no client found by provided clientId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Store store = new Store();
        store.setClientId(clientId);
        store.setStoreName(storeName);
        store.setStoreContactNo(storeContactNo);
        store.setRegistrationNo(registrationNo);
        store.setGstNo(gstNo);
        store.setCity(city);
        store.setPinCode(pinCode);
        store.setState(state);
        store.setCountry(country);
        store.setStoreAdminIds(storeAdminIds);
        store.onCreate();

        Store savedStore = cdRepository.saveStore(store);
        ObjectNode node  = objectMapper.createObjectNode();
        node.put("id", savedStore.getId());
        node.put("storeName", savedStore.getStoreName());
        node.put("storeContactNo", savedStore.getStoreContactNo());
        node.put("registrationNo", savedStore.getRegistrationNo());
        node.put("gstNo", savedStore.getGstNo());
        node.put("pinCode", store.getPinCode());
        node.put("city", savedStore.getCity());
        node.put("state", savedStore.getState());
        node.put("country", savedStore.getCountry());
        node.put("createdOn", String.valueOf(savedStore.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(savedStore.getModifiedOn()));
        node.put("createdBy",String.valueOf(savedStore.getCreatedBy()));
        node.put("updatedBy",savedStore.getModifiedBy());
        node.put("active",savedStore.getActive());
        return new ApiResponseHandler("store created successfully!!!", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }
}
