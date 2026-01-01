package com.example.blog.service;

import com.example.blog.dto.TodoDTO;

import java.util.List;

public interface TodoService {

    List<TodoDTO> findByMemberNo(int memberNo);

    TodoDTO findById(int id);

    void save(TodoDTO todo);

    void update(TodoDTO todo);

    void deleteById(int id);
}
