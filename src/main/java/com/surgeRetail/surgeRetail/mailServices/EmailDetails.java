package com.surgeRetail.surgeRetail.mailServices;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Data
public class EmailDetails {
    @Id
    private String id;
    private String recipient;
    private String mailBody;
    private String subject;
    private MultipartFile attachment;
    private Instant createdAt;
}
