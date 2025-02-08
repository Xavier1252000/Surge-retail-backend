package com.surgeRetail.surgeRetail.mailServices.services;

import com.surgeRetail.surgeRetail.mailServices.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {

    String sendSimpleMail(EmailDetails emailDetails);

    String sendEmailWithAttachment(EmailDetails emailDetails) throws MessagingException;
}
