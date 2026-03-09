package com.example.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${site.name}")
    private String siteName;

    @Override
    public void sendTemporaryPassword(String toEmail, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[" + siteName + "] 임시 비밀번호 안내");
        message.setText("안녕하세요.\n\n요청하신 임시 비밀번호는 다음과 같습니다.\n\n" +
                "임시 비밀번호: " + temporaryPassword + "\n\n" +
                "로그인 후 반드시 비밀번호를 변경해 주세요.");
        
        mailSender.send(message);
    }
}
