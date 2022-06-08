package com.auth.service.impl;

import com.auth.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {


    @Autowired
    private Environment env;
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    @Override
    public void sendEmail(String userEmail, String subject, String body) {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(userEmail);
//        mailMessage.setSubject(subject);
//        mailMessage.setFrom(env.getProperty("spring.mail.username"));
//        mailMessage.setText(body);
//
//        javaMailSender.send(mailMessage);
    }
}
