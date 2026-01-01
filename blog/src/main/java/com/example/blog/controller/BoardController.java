package com.example.blog.controller;

import com.example.blog.dto.BoardDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.MemberDTO;
import com.example.blog.service.BoardService;
import com.example.blog.service.CategoryService;
import com.example.blog.service.CommentService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired private BoardService boardService;
    @Autowired private CategoryService categoryService;
    @Autowired private CommentService commentService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer categoryId,
                       HttpSession session,
                       Model model) {

        int pageSize = 10;
        List<BoardDTO> boards;
        int total;

        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            boards = boardService.searchBoardsByCategoryAndKeyword(categoryId, keyword, page, pageSize);
            total = boardService.countBoardsByCategoryAndKeyword(categoryId, keyword);
        } else if (keyword != null && !keyword.isEmpty()) {
            boards = boardService.searchBoards(keyword, page, pageSize);
            total = boardService.countSearchBoards(keyword);
        } else if (categoryId != null) {
            boards = boardService.getBoardsByCategory(categoryId, page, pageSize);
            total = boardService.countBoardsByCategory(categoryId);
        } else {
            boards = boardService.getBoardsByPage(page, pageSize);
            total = boardService.countBoards();
        }

        for (BoardDTO board : boards) {
            int count = commentService.countByBoardId(board.getId());
            board.setCommentCount(count);
        }

        int totalPages = (int) Math.ceil((double) total / pageSize);
        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("categoryList", categoryService.getAll());
        model.addAttribute("categoryId", categoryId);

        return "board/list";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, Model model, HttpSession session) {
        BoardDTO board = boardService.getBoardById(id);
        if (board == null) {
            return "redirect:/board/list";
        }

        boardService.incrementViews(id);
        List<CommentDTO> comments = commentService.getCommentsByBoardId(id);

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);

        return "board/view";
    }

    @GetMapping("/write")
    public String writeForm(HttpSession session, Model model) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("categoryList", categoryService.getAll());
        model.addAttribute("writerId", loginUser.getId());
        return "board/write";
    }

    @PostMapping("/write")
    public String write(BoardDTO board, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }

        board.setWriter(loginUser.getId());
        String thumbnail = extractFirstImageUrl(board.getContent());
        board.setThumbnail(thumbnail);

        boardService.insertBoard(board);
        return "redirect:/board/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        BoardDTO board = boardService.getBoardById(id);
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");

        if (board == null || loginUser == null || !loginUser.getId().equals(board.getWriter())) {
            return "redirect:/board/view/" + id;
        }

        model.addAttribute("board", board);
        model.addAttribute("categoryList", categoryService.getAll());
        return "board/edit";
    }

    @PostMapping("/edit")
    public String edit(BoardDTO board, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        BoardDTO original = boardService.getBoardById(board.getId());

        if (original == null || loginUser == null || !loginUser.getId().equals(original.getWriter())) {
            return "redirect:/board/view/" + board.getId();
        }

        String thumbnail = extractFirstImageUrl(board.getContent());
        board.setThumbnail(thumbnail);

        boardService.updateBoard(board);
        return "redirect:/board/view/" + board.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        BoardDTO board = boardService.getBoardById(id);
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");

        if (board == null || loginUser == null || !loginUser.getId().equals(board.getWriter())) {
            return "redirect:/board/view/" + id;
        }

        boardService.deleteBoard(id);
        return "redirect:/board/list";
    }

    private String extractFirstImageUrl(String content) {
        try {
            Document doc = Jsoup.parse(content);
            Element img = doc.selectFirst("img");
            if (img != null) {
                return img.attr("src");
            }
        } catch (Exception e) {
            logger.warn("대표 이미지 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}
