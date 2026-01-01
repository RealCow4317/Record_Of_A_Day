package com.example.blog.dao;

import com.example.blog.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDAO {
    void addComment(CommentDTO comment);
    void deleteComment(int id);
    List<CommentDTO> getCommentsByBoardId(int boardId);
    CommentDTO getCommentById(int id);
    void updateComment(CommentDTO comment);
    int countByBoardId(int boardId);

}
