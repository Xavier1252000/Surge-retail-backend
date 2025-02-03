package com.surgeRetail.surgeRetail.security.service;

import com.surgeRetail.surgeRetail.document.User;
import com.surgeRetail.surgeRetail.repository.PublicApiRepository;
import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PublicApiRepository publicApiRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = publicApiRepository.findUserByUsernameOrEmail(username);
        if (user==null)
            throw new UsernameNotFoundException("user not found with provided username: "+username);

        return new UserDetailsImpl(user);
    }
}
