package com.example.blog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSchedulerService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 매일 저녁 8시(20:00)에 실행되는 스케줄러
     * cron 표현식: 초 분 시 일 월 요일
     * 0 0 20 * * ? = 매일 20시 0분 0초
     */
    @Scheduled(cron = "0 0 20 * * ?")
    public void sendEveningNotification() {
        logger.info("저녁 8시 알림 전송 시작");
        
        String message = "오늘 하루를 의미있게 마무리 하세요!";
        
        // JSON 형식으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/notifications", message);
        
        logger.info("저녁 8시 알림 전송 완료: {}", message);
    }
    
    /**
     * 테스트용: 수동으로 알림 전송 (개발/테스트 시 사용)
     * 실제 운영에서는 제거하거나 관리자만 접근 가능하도록 설정
     */
    // @Scheduled(fixedDelay = 30000) // 30초마다 테스트
    public void sendTestNotification() {
        logger.info("테스트 알림 전송");
        String message = "테스트: 오늘 하루를 의미있게 마무리 하세요!";
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
}

