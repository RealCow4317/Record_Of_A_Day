package com.example.blog.controller;

import com.example.blog.dto.BoardDTO;
import com.example.blog.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String home(Model model) {
        List<BoardDTO> recentBoards = boardService.getRecentBoards();
        model.addAttribute("recentBoards", recentBoards);
        return "index";
    }
}
