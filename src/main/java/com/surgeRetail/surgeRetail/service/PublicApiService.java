package com.surgeRetail.surgeRetail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgeRetail.surgeRetail.document.userAndRoles.SuperAdminInfo;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.mailServices.EmailDetails;
import com.surgeRetail.surgeRetail.mailServices.services.EmailService;
import com.surgeRetail.surgeRetail.mailServices.services.EmailServiceImpl;
import com.surgeRetail.surgeRetail.repository.ConfidentialApiRepository;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.security.jwt.JwtService;
import com.surgeRetail.surgeRetail.security.jwt.JwtToken;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class PublicApiService {

    private final PublicApiRepository publicApiRepository;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ConfidentialApiRepository confidentialApiRepository;

    public PublicApiService(PublicApiRepository publicApiRepository,
                            ObjectMapper objectMapper,
                            BCryptPasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager,
                            JwtService jwtService,
                            EmailServiceImpl emailService,
                            ConfidentialApiRepository confidentialApiRepository){
        this.publicApiRepository = publicApiRepository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.confidentialApiRepository = confidentialApiRepository;
    }


    public ResponseEntity<ApiResponseHandler> authenticateUser(String username, String password) throws MessagingException {
        User user = publicApiRepository.findUserByUsernameOrEmail(username);
        if (user==null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseHandler("authentication failed, user not found", null, ResponseStatus.UNAUTHORIZED, ResponseStatusCode.UNAUTHORIZED, true));
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String response = emailService.sendEmailWithImage(
                "ns1252000@gmail.com",
                "Subject: Test Email with Image",
                "Please find the attached image.",
                "/home/nikhil-shukla/Downloads/Resume Nikhil-.pdf"
        );

        String tokenString = jwtService.generateToken(username);
        JwtToken token = new JwtToken();
        token.setToken(tokenString);
        token.setUser(user);
        token.setExpirationDate(jwtService.extractExpiration(tokenString));
        token.setCreationDate(new Date());

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setSubject("new login");
        emailDetails.setRecipient(user.getEmailId());
        emailDetails.setMailBody("new login from the anonymous system");
        emailDetails.setCreatedAt(Instant.now());
        try {
            emailService.sendEmailWithAttachment(emailDetails);
        }catch (Exception e){
            System.out.println(e);
        }


        return ResponseEntity.ok(new ApiResponseHandler("authentication successful", token, ResponseStatus.SUCCESS, ResponseStatusCode.SUCCESS, false));
    }

    public ApiResponseHandler registerSuperUser(String firstName, String lastName, String username, String emailId, String mobileNo, String password, String superAdminSecret) {

        if (publicApiRepository.anySuperAdminExists()){
            return new ApiResponseHandler("superAdmin exists, login with the super admin first to fo forward", null, ResponseStatus.BAD_REQUEST, ResponseStatusCode.BAD_REQUEST, true);
        }

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
        roles.add(User.USER_ROLE_USER);
        roles.add(User.USER_ROLE_STORE_ADMIN);
        roles.add(User.USER_ROLE_SUPER_ADMIN);

        user.setRoles(roles);

        User savedUser = publicApiRepository.save(user);

        SuperAdminInfo superAdminInfo = new SuperAdminInfo();
        superAdminInfo.setSuperAdminSecret(passwordEncoder.encode(superAdminSecret));
        superAdminInfo.setUserId(savedUser.getId());
        superAdminInfo.setCreatedOn(Instant.now());
        superAdminInfo.setModifiedOn(Instant.now());
        superAdminInfo.setActive(true);

        publicApiRepository.saveSuperAdminInfo(superAdminInfo);

        HashMap<Object, Object> responseMap = new HashMap<>();
        responseMap.put("id", user.getId());
        responseMap.put("firstName", user.getFirstName());
        responseMap.put("lastName", user.getLastName());
        responseMap.put("username", user.getUsername());
        responseMap.put("emailId", user.getEmailId());
        responseMap.put("createdOn", user.getCreatedOn());

        return new ApiResponseHandler("superAdmin registered",responseMap, ResponseStatus.CREATED, ResponseStatusCode.CREATED, false);
    }
}
