package com.example.blog.controller;

import com.example.blog.dto.ImageDTO;
import com.example.blog.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RestController
public class UploadController {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload/image")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {

        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String savedName = uuid + "_" + originalFilename;

        File dest = Paths.get(uploadDir, savedName).toFile();

        file.transferTo(dest);

        String imageUrl = request.getContextPath() + "/upload/" + savedName;

        // DB 저장
        ImageDTO image = new ImageDTO();
        image.setFileName(savedName);
        image.setFilePath(imageUrl);
        imageService.saveImage(image);

        return imageUrl;
    }
}