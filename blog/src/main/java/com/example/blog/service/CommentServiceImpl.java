package com.example.blog.service;

import com.example.blog.dao.CommentDAO;
import com.example.blog.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Override
    public void addComment(CommentDTO comment) {
        commentDAO.addComment(comment);
    }

    @Override
    public void deleteComment(int id) {
        commentDAO.deleteComment(id);
    }

    @Override
    public List<CommentDTO> getCommentsByBoardId(int boardId) {
        List<CommentDTO> allComments = commentDAO.getCommentsByBoardId(boardId);
        Map<Integer, CommentDTO> commentMap = new java.util.HashMap<>();
        List<CommentDTO> rootComments = new java.util.ArrayList<>();

        for (CommentDTO comment : allComments) {
            comment.setReplies(new java.util.ArrayList<>());
            commentMap.put(comment.getId(), comment);
        }

        for (CommentDTO comment : allComments) {
            if (comment.getParentId() != null && comment.getParentId() != 0) {
                CommentDTO parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    parent.getReplies().add(comment);
                }
            } else {
                rootComments.add(comment);
            }
        }
        return rootComments;
    }
    @Override
    public CommentDTO getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }

    @Override
    public void updateComment(CommentDTO comment) {
        commentDAO.updateComment(comment);
    }

    public int countByBoardId(int boardId) {
        return commentDAO.countByBoardId(boardId);
    }

}
