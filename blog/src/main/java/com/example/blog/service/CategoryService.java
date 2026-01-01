package com.example.blog.service;

import java.util.List;
import com.example.blog.dto.CategoryDTO;

public interface CategoryService {
    List<CategoryDTO> getAll();
    void add(String name);
    void delete(int id);
}
