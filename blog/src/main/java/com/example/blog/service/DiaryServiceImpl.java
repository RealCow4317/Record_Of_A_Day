package com.example.blog.service;

import com.example.blog.dao.DiaryDAO;
import com.example.blog.dto.DiaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Date; // Add this import
import java.util.List;
import java.util.UUID;

@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiaryDAO diaryDAO;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Override
    public void saveDiary(DiaryDTO diary, MultipartFile imageFile) throws Exception {
        // Handle file upload
        String imagePath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            File dest = new File(uploadDir + storedFilename);
            
            // Ensure the upload directory exists
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            imageFile.transferTo(dest);
            imagePath = "/upload/" + storedFilename;
        }

        // Check for existing diary entry
        DiaryDTO existingDiary = diaryDAO.findByMemberNoAndDiaryDate(diary.getMemberNo(), diary.getDiaryDate());

        if (existingDiary != null) {
            // Update existing entry
            existingDiary.setContent(diary.getContent());
            if (imagePath != null) { // Only update image if a new one was provided
                existingDiary.setImagePath(imagePath);
            }
            diaryDAO.update(existingDiary);
        } else {
            // Insert new entry
            diary.setImagePath(imagePath);
            diaryDAO.save(diary);
        }
    }

    @Override
    public List<DiaryDTO> findByMemberNoAndDateRange(int memberNo, String startDate, String endDate) {
        return diaryDAO.findByMemberNoAndDateRange(memberNo, startDate, endDate);
    }

    @Override
    public DiaryDTO findByMemberNoAndDiaryDate(int memberNo, Date diaryDate) {
        return diaryDAO.findByMemberNoAndDiaryDate(memberNo, diaryDate);
    }
}
