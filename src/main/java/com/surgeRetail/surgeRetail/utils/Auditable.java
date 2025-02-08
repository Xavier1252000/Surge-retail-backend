package com.surgeRetail.surgeRetail.utils;

import com.surgeRetail.surgeRetail.security.UserDetailsImpl;
import lombok.Data;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

@Data
public class Auditable {
    @CreatedDate
    private Instant createdOn;

    @LastModifiedDate
    private Instant modifiedOn;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;

    private Boolean active = true;

    public void onCreate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            this.createdBy = ((UserDetailsImpl) authentication.getPrincipal()).getId();
            this.modifiedBy = this.createdBy;
        }
        this.createdOn = Instant.now();
        this.modifiedOn = Instant.now();
        this.active = true;
    }

    public void onUpdate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            this.modifiedBy = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        }
        this.modifiedOn = Instant.now();
    }
}
