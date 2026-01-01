package com.example.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @PostMapping("/send")
    @ResponseBody
    public Map<String, Object> sendNotification(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            result.put("success", false);
            result.put("message", "관리자 권한이 필요합니다.");
            return result;
        }

        String message = "오늘 하루를 의미있게 마무리 하세요!";
        messagingTemplate.convertAndSend("/topic/notifications", message);

        result.put("success", true);
        result.put("message", "알림이 전송되었습니다.");
        return result;
    }
}

