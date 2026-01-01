package com.example.blog.service;

import com.example.blog.dao.ImageDAO;
import com.example.blog.dto.ImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageDAO imageDAO;

    @Override
    public void saveImage(ImageDTO image) {
        imageDAO.insertImage(image);
    }
}
