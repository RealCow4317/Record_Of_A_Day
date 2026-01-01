package com.example.blog.service;

import com.example.blog.dto.DiaryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date; // Add this import
import java.util.List;

public interface DiaryService {
    void saveDiary(DiaryDTO diary, MultipartFile imageFile) throws Exception;

    List<DiaryDTO> findByMemberNoAndDateRange(int memberNo, String startDate, String endDate);

    DiaryDTO findByMemberNoAndDiaryDate(int memberNo, Date diaryDate); // New method
}
