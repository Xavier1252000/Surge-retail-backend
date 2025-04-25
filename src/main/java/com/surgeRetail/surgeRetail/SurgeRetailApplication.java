package com.surgeRetail.surgeRetail;

import com.surgeRetail.surgeRetail.repository.PermissionApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SurgeRetailApplication {

	@Autowired
	PermissionApiRepository permissionApiRepository;


	public static void main(String[] args) {
		SpringApplication.run(SurgeRetailApplication.class, args);
	}
}
