package com.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendEmail(String to, String subject, String body){
        boolean isMailsent = false;

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper= new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            javaMailSender.send(mimeMessage);

            isMailsent=true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return isMailsent;
    }
}
