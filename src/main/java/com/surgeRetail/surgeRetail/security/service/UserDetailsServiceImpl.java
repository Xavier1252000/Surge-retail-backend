package com.surgeRetail.surgeRetail.security.service;

import com.surgeRetail.surgeRetail.document.userAndRoles.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import com.surgeRetail.surgeRetail.repository.ConfidentialApiRepository;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PublicApiRepository publicApiRepository;
    private final ConfidentialApiRepository confidentialApiRepository;

    public UserDetailsServiceImpl(PublicApiRepository publicApiRepository,
                                  ConfidentialApiRepository confidentialApiRepository){
        this.publicApiRepository = publicApiRepository;
        this.confidentialApiRepository = confidentialApiRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = publicApiRepository.findUserByUsernameOrEmail(username);
        if (user==null)
            throw new UsernameNotFoundException("user not found with provided username: "+username);

        List<Store> stores = new ArrayList<>();
        if (user.getRoles().contains(User.USER_ROLE_SUPER_ADMIN)) {
            stores = confidentialApiRepository.findAllStores();
        }

        if (user.getRoles().contains(User.USER_ROLE_CLIENT) || !user.getRoles().contains(User.USER_ROLE_SUPER_ADMIN)) { //should be client but not superadmin
            stores = confidentialApiRepository.getStoresByClientId(user.getId());
        }

        if (user.getRoles().contains(User.USER_ROLE_STORE_ADMIN) || !user.getRoles().contains(User.USER_ROLE_SUPER_ADMIN) || !user.getRoles().contains(User.USER_ROLE_CLIENT)){
            stores = confidentialApiRepository.getStoreByStoreAdminId(user.getId());
        }
        return new UserDetailsImpl(user, stores);
    }
}
