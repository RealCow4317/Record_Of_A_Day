package com.example.blog.service;

import com.example.blog.dao.DiaryDAO;
import com.example.blog.dto.DiaryDTO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails; // Import Thumbnailator
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiaryDAO diaryDAO;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Override
    public void saveDiary(DiaryDTO diary, MultipartFile imageFile) throws Exception {
        // Handle file upload
        String imagePath = null;
        String thumbnailPath = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String storedFilename = uuid + "_" + originalFilename;
            String thumbnailFilename = "thumb_" + uuid + "_" + originalFilename;

            // Ensure the upload directory exists
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            File uploadDirFile = uploadPath.toFile();
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            Path destPath = uploadPath.resolve(storedFilename);
            Path thumbDestPath = uploadPath.resolve(thumbnailFilename);

            // Save original image
            imageFile.transferTo(destPath);
            imagePath = "/upload/" + storedFilename;

            // Generate thumbnail (300x300)
            try {
                Thumbnails.of(destPath.toFile())
                        .size(300, 300)
                        .toFile(thumbDestPath.toFile());
                thumbnailPath = "/upload/" + thumbnailFilename;
            } catch (Exception e) {
                // If thumbnail generation fails, fallback to original image or handle error
                e.printStackTrace();
                thumbnailPath = imagePath; 
            }
        }

        // Check for existing diary entry
        DiaryDTO existingDiary = diaryDAO.findByMemberNoAndDiaryDate(diary.getMemberNo(), diary.getDiaryDate());

        if (existingDiary != null) {
            // Update existing entry
            existingDiary.setContent(diary.getContent());
            if (imagePath != null) { // Only update image if a new one was provided
                existingDiary.setImagePath(imagePath);
                existingDiary.setThumbnailPath(thumbnailPath);
            }
            diaryDAO.update(existingDiary);
        } else {
            // Insert new entry
            diary.setImagePath(imagePath);
            diary.setThumbnailPath(thumbnailPath);
            diaryDAO.save(diary);
        }
    }

    @Override
    public List<DiaryDTO> findByMemberNoAndDateRange(int memberNo, String startDate, String endDate) {
        return diaryDAO.findByMemberNoAndDateRange(memberNo, startDate, endDate);
    }

    @Override
    public DiaryDTO findByMemberNoAndDiaryDate(int memberNo, Date diaryDate) {
        log.debug("Finding diary for memberNo: {} on date: {}", memberNo, diaryDate);
        return diaryDAO.findByMemberNoAndDiaryDate(memberNo, diaryDate);
    }

    @Override
    public void deleteDiary(int memberNo, Date diaryDate) {
        log.info("Attempting to delete diary for memberNo: {}, date: {}", memberNo, diaryDate);
        DiaryDTO diary = diaryDAO.findByMemberNoAndDiaryDate(memberNo, diaryDate);
        if (diary != null) {
            // Delete physical files
            deletePhysicalFile(diary.getImagePath());
            deletePhysicalFile(diary.getThumbnailPath());
            
            // Delete database record
            diaryDAO.deleteByMemberNoAndDiaryDate(memberNo, diaryDate);
            log.info("Successfully deleted diary record and files for date: {}", diaryDate);
        } else {
            log.warn("No diary entry found to delete for date: {}", diaryDate);
        }
    }

    private void deletePhysicalFile(String webPath) {
        if (webPath != null && webPath.startsWith("/upload/")) {
            String filename = webPath.substring("/upload/".length());
            try {
                Path filePath = Paths.get(uploadDir).resolve(filename).toAbsolutePath().normalize();
                File file = filePath.toFile();
                if (file.exists()) {
                    if (file.delete()) {
                        log.debug("Deleted physical file: {}", filePath);
                    } else {
                        log.warn("Failed to delete physical file: {}", filePath);
                    }
                }
            } catch (Exception e) {
                log.error("Error deleting file {}: {}", webPath, e.getMessage());
            }
        }
    }
}
