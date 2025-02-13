package com.surgeRetail.surgeRetail.security;

import com.surgeRetail.surgeRetail.document.Item.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class UserDetailsImpl implements UserDetails {
    private User user;
    private List<Store> stores;

    public UserDetailsImpl(User user, List<Store> stores){
        this.user = user;
        this.stores = stores;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        user.getRoles().forEach(e->{
            grantedAuthorities.add(new SimpleGrantedAuthority(e));
        });
        return grantedAuthorities;
    }

    public String getId(){return this.user.getId();}

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
