package com.example.blog.service;

public interface MailService {
    void sendTemporaryPassword(String toEmail, String temporaryPassword);
}
