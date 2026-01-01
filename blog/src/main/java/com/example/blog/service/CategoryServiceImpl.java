package com.example.blog.service;

import java.util.List;
import com.example.blog.dao.CategoryDAO;
import com.example.blog.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public List<CategoryDTO> getAll() {
        return categoryDAO.getAllCategories();
    }

    @Override
    public void add(String name) {
        categoryDAO.insertCategory(name);
    }

    @Override
    public void delete(int id) {
        categoryDAO.deleteCategory(id);
    }
}
