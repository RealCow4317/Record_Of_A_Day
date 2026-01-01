package com.example.blog.dao;

import com.example.blog.dto.CategoryDTO;
import java.util.List;

public interface CategoryDAO {
    List<CategoryDTO> getAllCategories();
    void insertCategory(String name);
    void deleteCategory(int id);
}
