package com.example.blog.dao;

import com.example.blog.dto.TodoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TodoDAO {

    List<TodoDTO> findByMemberNo(@Param("memberNo") int memberNo);

    TodoDTO findById(@Param("id") int id);

    void save(TodoDTO todo);

    void update(TodoDTO todo);

    void deleteById(@Param("id") int id);
}
