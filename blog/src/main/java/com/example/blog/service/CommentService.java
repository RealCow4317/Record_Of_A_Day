package com.example.blog.service;

import com.example.blog.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    void addComment(CommentDTO comment);
    void deleteComment(int id);
    List<CommentDTO> getCommentsByBoardId(int boardId);
    CommentDTO getCommentById(int id);
    void updateComment(CommentDTO comment);
    int countByBoardId(int boardId);


}
