package com.example.lotterysystem.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailUtil {
    private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);
    @Value(value = "${spring.mail.username}")
    private String from;
    @Autowired
    private JavaMailSender mailSender;

    public Boolean sendSampleMail(String to, String subject, String context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(context);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            logger.error(" 向 {} 发送邮件失败！ ", to, e);
            return false;
        }
        return true;
    }
}