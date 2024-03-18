package com.ssafy.jariyo.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@Component
public class EmailConfig {
    private String type = "text/html; charset=utf-8";

    @Value("${email.address}")
    private String emailAddress;

    @Value("${email.password}")
    private String password;

    public void sendMail(String email, String title, String content) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, password);
            }
        };

        Session session = Session.getInstance(properties, auth);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAddress, "TRV"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject(title);

            // HTML 콘텐츠와 이미지 포함
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 20px auto; border: 1px solid #ddd; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                    "<div style='background-color: #f8f9fa; padding: 20px; text-align: center;'>" +
                    "<img src='https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/mainlogo.png' alt='Company Logo' style='width: 100px;'>" +
                    "</div>" +
                    "<div style='padding: 20px;'>" +
                    "<h1 style='color: #333;'>Reservation Confirmed!</h1>" +
                    "<p style='color: #555;'>Hello, your reservation for <b>" + title + "</b> is confirmed.</p>" +
                    "<p style='color: #555;'>" + content + "</p>" +
                    "<a href='https://b201-front.hyegpfud.duckdns.org/#/' style='display: inline-block; padding: 10px 20px; background-color: #f89e6b; color: white; text-decoration: none; border-radius: 5px;'>Visit Our Site</a>" +
                    "</div>" +
                    "<div style='background-color: #f89e6b; color: white; padding: 20px; text-align: center;'>" +
                    "Thank you for choosing us!" +
                    "</div>" +
                    "</div>";

            message.setContent(htmlContent, type);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append((char) ((int) (Math.random() * 57) + 65));
        }
        return sb.toString();
    }
}


