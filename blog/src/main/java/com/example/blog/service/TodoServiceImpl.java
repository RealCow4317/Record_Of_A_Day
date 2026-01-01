package com.example.blog.service;

import com.example.blog.dao.TodoDAO;
import com.example.blog.dto.TodoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoDAO todoDAO;

    @Override
    public List<TodoDTO> findByMemberNo(int memberNo) {
        List<TodoDTO> allTodos = todoDAO.findByMemberNo(memberNo);
        Map<Integer, TodoDTO> todoMap = new HashMap<>();
        List<TodoDTO> rootTodos = new ArrayList<>();

        for (TodoDTO todo : allTodos) {
            todoMap.put(todo.getId(), todo);
        }

        for (TodoDTO todo : allTodos) {
            Integer parentId = todo.getParentId();
            if (parentId != null) {
                TodoDTO parent = todoMap.get(parentId);
                if (parent != null) {
                    parent.getSubTasks().add(todo);
                }
            } else {
                rootTodos.add(todo);
            }
        }
        return rootTodos;
    }

    @Override
    public TodoDTO findById(int id) {
        return todoDAO.findById(id);
    }

    @Override
    public void save(TodoDTO todo) {
        todoDAO.save(todo);
    }

    @Override
    public void update(TodoDTO todo) {
        todoDAO.update(todo);
    }

    @Override
    public void deleteById(int id) {
        todoDAO.deleteById(id);
    }
}
