package com.example.blog.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.sql.Timestamp;

@Data
public class ScheduleDTO {
    private Integer id; // Integer로 변경하여 null 허용
    private int memberNo;
    private String title;
    private String content;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    
    private String color;
    private Timestamp createdAt;

    // MyBatis 연동을 위해 java.sql.Date로 변환하는 헬퍼 메서드
    public java.sql.Date getSqlStartDate() {
        return startDate != null ? new java.sql.Date(startDate.getTime()) : null;
    }
    
    public java.sql.Date getSqlEndDate() {
        return endDate != null ? new java.sql.Date(endDate.getTime()) : null;
    }
}
