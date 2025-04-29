package com.surgeRetail.surgeRetail.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.master.RoleMaster;
import com.surgeRetail.surgeRetail.document.userAndRoles.ClientDetails;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.repository.ConfidentialApiRepository;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.utils.AppUtils;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class ConfidentialApiService {

    private final ConfidentialApiRepository confidentialApiRepository;
    private final PublicApiRepository publicApiRepository;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public ConfidentialApiService(ConfidentialApiRepository confidentialApiRepository,
                                  PublicApiRepository publicApiRepository,
                                  ObjectMapper objectMapper,
                                  BCryptPasswordEncoder passwordEncoder){
        this.confidentialApiRepository = confidentialApiRepository;
        this.publicApiRepository = publicApiRepository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponseHandler registerUser(String firstName, String lastName, String username, String emailId, String mobileNo, String password) {

        if (!publicApiRepository.anySuperAdminExists())
            return new ApiResponseHandler("First user must be super-admin", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (publicApiRepository.userExistByUsername(username))
            return new ApiResponseHandler("user already exists with provided username", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (publicApiRepository.userExistByEmailId(emailId))
            return new ApiResponseHandler("user exists with provided emailId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (publicApiRepository.userExistByMobileNo(mobileNo))
            return new ApiResponseHandler("mobile number already registered", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Set<String> role = new HashSet<>();  //  assigning user role by default
        role.add(User.USER_ROLE_USER);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailId(emailId);
        user.setMobileNo(mobileNo);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(role);
        user.setCreatedOn(Instant.now());
        user.setModifiedOn(Instant.now());
        user.setActive(true);

        publicApiRepository.save(user);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("id",user.getId());
        node.put("firstName", user.getFirstName());
        node.put("lastName",user.getLastName());
        node.put("emailId",user.getEmailId());
        node.put("username",user.getUsername());
        node.put("mobileNo",user.getMobileNo());
        node.set("roles", objectMapper.valueToTree(user.getRoles()));
        node.put("createdOn",String.valueOf(user.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(user.getModifiedOn()));
        node.put("active",user.isActive());

        return new ApiResponseHandler("user registered successfully with id: "+user.getId(), node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler registerClient(String firstName, String lastName, String emailId, String mobileNo, String username, String password) {

        if (publicApiRepository.userExistByUsername(username))
            return new ApiResponseHandler("client already exists with provided username", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (publicApiRepository.userExistByEmailId(emailId))
            return new ApiResponseHandler("client already registered with provided emailId", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (publicApiRepository.userExistByMobileNo(mobileNo))
            return new ApiResponseHandler("client already registered with provided mobileNo", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        Set<String> role = new HashSet<>();  //  assigning user role by default
        role.add(User.USER_ROLE_CLIENT);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailId(emailId);
        user.setMobileNo(mobileNo);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(role);
        user.setCreatedOn(Instant.now());
        user.setModifiedOn(Instant.now());
        user.setActive(true);

        publicApiRepository.save(user);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("id",user.getId());
        node.put("firstName", user.getFirstName());
        node.put("lastName",user.getLastName());
        node.put("emailId",user.getEmailId());
        node.put("username",user.getUsername());
        node.put("mobileNo",user.getMobileNo());
        node.set("roles", objectMapper.valueToTree(user.getRoles()));
        node.put("createdOn",String.valueOf(user.getCreatedOn()));
        node.put("modifiedOn", String.valueOf(user.getModifiedOn()));
        node.put("active",user.isActive());

        return new ApiResponseHandler("client registered successfully", node, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }


    public ApiResponseHandler registerSuperUser(String firstName, String lastName, String username, String emailId, String mobileNo, String password, String superAdminSecret) {

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailId(emailId);
        user.setMobileNo(mobileNo);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setCreatedOn(Instant.now());
        user.setModifiedOn(Instant.now());

        Set<String> roles = new HashSet<>();
        roles.add(User.USER_ROLE_SUPER_ADMIN);

        user.setRoles(roles);

        User savedUser = publicApiRepository.save(user);

        HashMap<Object, Object> responseMap = new HashMap<>();
        responseMap.put("id", user.getId());
        responseMap.put("firstName", user.getFirstName());
        responseMap.put("lastName", user.getLastName());
        responseMap.put("username", user.getUsername());
        responseMap.put("emailId", user.getEmailId());
        responseMap.put("createdOn", user.getCreatedOn());

        return new ApiResponseHandler("superAdmin registered",responseMap, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler registerUserWithCustomRoles(String firstName, String lastName, String emailId, String mobileNo, String username, String password, String superAdminSecret, String clientSecret) {
        return new ApiResponseHandler("customUser created successfully!!!", null, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler createRole(String role, String description) {
        RoleMaster roleMaster = new RoleMaster();
        roleMaster.setRole(role);
        roleMaster.setDescription(description);
        RoleMaster savedRole = confidentialApiRepository.saveRoleMaster(roleMaster);
        return new ApiResponseHandler("role created", savedRole, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }
}
