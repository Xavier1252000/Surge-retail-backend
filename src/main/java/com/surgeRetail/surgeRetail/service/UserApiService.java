package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.surgeRetail.surgeRetail.document.userAndRoles.ClientDetails;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.repository.ConfidentialApiRepository;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.repository.UserApiRepository;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import com.surgeRetail.surgeRetail.utils.AppUtils;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class UserApiService {
    private final ObjectMapper objectMapper;
    private final ConfidentialApiRepository confidentialApiRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserApiRepository userApiRepository;
    private final PublicApiRepository publicApiRepository;

    public UserApiService(ObjectMapper objectMapper,
                          ConfidentialApiRepository confidentialApiRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          UserApiRepository userApiRepository,
                          PublicApiRepository publicApiRepository
                          ){
        this.objectMapper = objectMapper;
        this.confidentialApiRepository = confidentialApiRepository;
        this.passwordEncoder = passwordEncoder;
        this.userApiRepository = userApiRepository;
        this.publicApiRepository = publicApiRepository;

    }

    public ApiResponseHandler getAllUsers(Integer index, Integer itemPerIndex, List<String> userIds, List<String> roles, Boolean active, Instant fromDate, Instant toDate) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
//        if (user.getRoles().contains(User.USER_ROLE_CLIENT)){
//
//        }

        List<User> allUsers = confidentialApiRepository.getAllUsers(index, itemPerIndex, userIds, roles, active, fromDate, toDate);
        ArrayNode arrayNode = objectMapper.createArrayNode();
        allUsers.forEach(e->{
            try {
                ObjectNode node = AppUtils.mapObjectToObjectNode(e);
                arrayNode.add(node);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        });

        return new ApiResponseHandler("All Users", arrayNode, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false);
    }


    public ApiResponseHandler registerCustomUser(String firstName, String lastName, String emailId, String mobileNo, String username, String password, Set<String> roles) {

        if(!userApiRepository.existRoleByRoleName(roles))
            return new ApiResponseHandler("please provide valid roles", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        if (roles.contains(User.USER_ROLE_SUPER_ADMIN) && roles.size()>1)
            return new ApiResponseHandler("You can't select multiple roles with SUPER ADMIN role", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailId(emailId);
        user.setMobileNo(mobileNo);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedOn(Instant.now());
        user.setModifiedOn(Instant.now());

        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setCreatedBy(principal.getId());
        user.setModifiedBy(principal.getId());
        user.setRoles(roles);
        user.setActive(true);

        publicApiRepository.save(user);

        if (user.getRoles().contains(User.USER_ROLE_CLIENT)) {
            ClientDetails client = new ClientDetails();
            client.setUserId(user.getId());
            client.setId(user.getId());
            client.onCreate();
            ClientDetails clientWithHighestNumId = userApiRepository.getClientWithHighestNumId();
            client.setNumericId(clientWithHighestNumId == null ? 0 : clientWithHighestNumId.getNumericId() + 1);
            userApiRepository.saveClient(client);
        }

        HashMap<Object, Object> responseMap = new HashMap<>();
        responseMap.put("id", user.getId());
        responseMap.put("firstName", user.getFirstName());
        responseMap.put("lastName", user.getLastName());
        responseMap.put("username", user.getUsername());
        responseMap.put("emailId", user.getEmailId());
        responseMap.put("roles", user.getRoles());
        responseMap.put("contactNo", user.getMobileNo());
        responseMap.put("createdOn", user.getCreatedOn());
        if (roles.contains(User.USER_ROLE_CLIENT)) {
            return new ApiResponseHandler("client registered successfully, please add client details",responseMap, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
        }
        return new ApiResponseHandler("User registered with role",responseMap, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ApiResponseHandler addClientDetails(String userId, String displayName, String secondaryEmail, String alternateContactNo, String languagePreference, String timeZone, String businessRegistrationNo, String businessType, String country, String state, String city, String postalCode, String address) {
        User client = confidentialApiRepository.findUserRegAsClientByUserId(userId);
        if (client == null)
            return new ApiResponseHandler("user might not be registered as client", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);

        ClientDetails cd = userApiRepository.findClientByUserId(userId);

        cd.setId(userId);
        cd.setDisplayName(displayName);
        cd.setSecondaryEmail(secondaryEmail);
        cd.setAlternateContactNo(alternateContactNo);
        cd.setLanguagePreference(languagePreference);
        cd.setTimeZone(timeZone);
        cd.setBusinessRegistrationNo(businessRegistrationNo);
        cd.setBusinessType(businessType);
        cd.setCountry(country);
        cd.setState(state);
        cd.setCity(city);
        cd.setPostalCode(postalCode);
        cd.setAddress(address);

        cd.setSubscriptionStatus(false);
        cd.setCurrentSubscriptionDetails(null);
        cd.onUpdate();

        ClientDetails clientDetails = userApiRepository.saveClientDetails(cd);

        return new ApiResponseHandler("client details saved successfully", clientDetails, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }

    public ResponseEntity<ApiResponseHandler> getClientByUserId(String userId) {
        ClientDetails cd= userApiRepository.getClientByUserId(userId);
        return new ResponseEntity<>(new ApiResponseHandler("success", cd, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false), HttpStatus.OK);
    }
}
