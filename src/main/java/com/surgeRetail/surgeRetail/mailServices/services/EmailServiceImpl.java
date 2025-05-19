package com.surgeRetail.surgeRetail.mailServices.services;

import com.surgeRetail.surgeRetail.mailServices.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService{

    @Value("${spring.mail.username}")
    private String sender;

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }
    @Override
    public String sendSimpleMail(EmailDetails details)
    {

        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMailBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }


    @Override
    public String sendEmailWithAttachment(EmailDetails emailDetails) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setFrom(sender);
        messageHelper.setTo(emailDetails.getRecipient());
        messageHelper.setSubject(emailDetails.getSubject());

        // Get file path from EmailDetails (assuming it contains the file path)
        String filePath = "/home/nikhil-shukla/Downloads/tree2.jpg";
        try {
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    FileSystemResource fileSystemResource = new FileSystemResource(file);
                    messageHelper.addAttachment(file.getName(), fileSystemResource);

                    } else {
                    System.err.println("Attachment file not found: " + filePath);
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }

        try {
            javaMailSender.send(mimeMessage);
        }catch (Exception e){
        }

        return "Mail sent successfully!";
    }



    @Override
    public String sendEmailWithImage(String recipient, String subject, String text, String imagePath) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        try {
            messageHelper.setFrom(sender);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(text, true); // `true` enables HTML content

            // Attach Image File
            File file = new File(imagePath);
            if (file.exists()) {
                FileSystemResource fileResource = new FileSystemResource(file);
                messageHelper.addAttachment(file.getName(), fileResource);
            } else {
                return "Image file not found!";
            }

            javaMailSender.send(mimeMessage);
            System.out.println("mail sent----------------------------------->");
            return "Mail sent successfully with image attachment!";
        } catch (Exception e) {
            System.out.println("error in mail sent---------------------->");
            return "Error while sending mail: " + e.getMessage();
        }
    }


}
