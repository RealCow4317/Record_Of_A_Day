package com.example.blog.controller;

import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.MemberDTO;
import com.example.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public String addComment(
            CommentDTO comment,
            @RequestParam(value = "parentId", defaultValue = "0", required = false) int parentId,
            HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null){
            return "redirect:/member/login";
    }
        comment.setWriter(loginUser.getId());
        comment.setParentId(parentId);
        commentService.addComment(comment);
        return "redirect:/board/view/" + comment.getBoardId();
}

    @GetMapping("/delete/{id}/{postId}")
    public String deleteComment(@PathVariable int id, @PathVariable int postId, HttpSession session) {

        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");

        if(loginUser == null){
            return "redirect:/";
        }

        CommentDTO comment = commentService.getCommentById(id);

        if(comment != null){
            boolean isWriter = comment.getWriter().equals(loginUser.getId());
            boolean isAdmin = loginUser.isAdmin();

            if(isWriter || isAdmin){
                commentService.deleteComment(id);
            }
        }

        return "redirect:/board/view/" + postId;
    }

    @PostMapping("/edit")
    public String edit(CommentDTO comment, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");

        CommentDTO dbComment = commentService.getCommentById(comment.getId());

        if (loginUser != null && dbComment != null && dbComment.getWriter().equals(loginUser.getId())) {
            comment.setWriter(loginUser.getId());
            commentService.updateComment(comment);
        }

        return "redirect:/board/view/" + comment.getBoardId();
    }

}
