package com.example.blog.controller.admin;

import com.example.blog.dto.BoardDTO;
import com.example.blog.dto.CategoryDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.MemberDTO;
import com.example.blog.service.BoardService;
import com.example.blog.service.CategoryService;
import com.example.blog.service.CommentService;
import com.example.blog.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private MemberService memberService;
    @Autowired private CategoryService categoryService;
    @Autowired private BoardService boardService;
    @Autowired private CommentService commentService;

    @ModelAttribute
    public void checkAdmin(HttpSession session,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            session.setAttribute("redirectAfterLogin", request.getRequestURI());
            response.sendRedirect("/member/login");
        }
    }

    @GetMapping
    public String index(Model model) {
        List<MemberDTO> users = memberService.getAllMembers();
        if (users.size() > 5) users = users.subList(0, 5);
        List<BoardDTO> recentPosts = boardService.getBoardsByPage(1, 5);
        List<CategoryDTO> categories = categoryService.getAll();
        if (categories.size() > 5) categories = categories.subList(0, 5);

        model.addAttribute("users", users);
        model.addAttribute("recentPosts", recentPosts);
        model.addAttribute("categories", categories);
        return "admin/index";
    }

    @GetMapping("/users")
    public String userList(@RequestParam(required = false) String keyword, Model model) {
        List<MemberDTO> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = memberService.searchMembers(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            users = memberService.getAllMembers();
        }
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @GetMapping("/users/delete/{memberNo}")
    public String deleteUser(@PathVariable int memberNo) {
        memberService.deleteMember(memberNo);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{memberNo}")
    public String editUserForm(@PathVariable int memberNo, Model model) {
        MemberDTO user = memberService.getMemberByNo(memberNo);
        model.addAttribute("user", user);
        return "admin/users/editForm";
    }

    @PostMapping("/users/edit")
    public String editUser(MemberDTO user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            MemberDTO original = memberService.getMember(user.getId());
            user.setPassword(original.getPassword());
        }
        memberService.updateMember(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/add")
    public String addUserForm() {
        return "admin/users/addForm";
    }

    @PostMapping("/users/add")
    public String addUser(MemberDTO user) {
        memberService.register(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        return "admin/category/list";
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String name) {
        categoryService.add(name);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable int id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/posts")
    public String postList(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Integer categoryId,
                           Model model) {

        int pageSize = 10;
        List<BoardDTO> posts;
        int total;

        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            posts = boardService.searchBoardsByCategoryAndKeyword(categoryId, keyword, page, pageSize);
            total = boardService.countBoardsByCategoryAndKeyword(categoryId, keyword);
            model.addAttribute("keyword", keyword);
            model.addAttribute("selectedCategory", categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            posts = boardService.searchBoards(keyword, page, pageSize);
            total = boardService.countSearchBoards(keyword);
            model.addAttribute("keyword", keyword);
        } else if (categoryId != null) {
            posts = boardService.getBoardsByCategory(categoryId, page, pageSize);
            total = boardService.countBoardsByCategory(categoryId);
            model.addAttribute("selectedCategory", categoryId);
        } else {
            posts = boardService.getBoardsByPage(page, pageSize);
            total = boardService.countBoards();
        }

        for (BoardDTO post : posts) {
            int commentCount = commentService.countByBoardId(post.getId());
            post.setCommentCount(commentCount);
        }

        int totalPages = (int) Math.ceil((double) total / pageSize);

        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("categoryId", categoryId);

        return "admin/posts/list";
    }


    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable int id) {
        boardService.deleteBoard(id);
        return "redirect:/admin/posts";
    }

    @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable int id, Model model) {
        BoardDTO board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        model.addAttribute("categoryList", categoryService.getAll());
        return "admin/posts/edit";
    }

    @PostMapping("/posts/edit")
    public String editPost(BoardDTO board) {
        boardService.updateBoard(board);
        return "redirect:/admin/posts";
    }

    @GetMapping("/posts/write")
    public String writePostForm(Model model) {
        model.addAttribute("categoryList", categoryService.getAll());
        return "admin/posts/write";
    }

    @PostMapping("/posts/write")
    public String writePost(BoardDTO board, HttpSession session) {
        MemberDTO admin = (MemberDTO) session.getAttribute("loginUser");
        board.setWriter(admin.getId());
        boardService.insertBoard(board);
        return "redirect:/admin/posts";
    }

    @GetMapping("/posts/view/{id}")
    public String viewPost(@PathVariable int id, Model model) {
        BoardDTO board = boardService.getBoardById(id);
        List<CommentDTO> comments = commentService.getCommentsByBoardId(id);

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        return "admin/posts/view";
    }
}
