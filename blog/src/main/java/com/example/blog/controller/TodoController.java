package com.example.blog.controller;

import com.example.blog.dto.MemberDTO;
import com.example.blog.dto.TodoDTO;
import com.example.blog.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }

        List<TodoDTO> todos = todoService.findByMemberNo(loginUser.getMemberNo());
        model.addAttribute("todos", todos);

        return "todo/list";
    }

    @PostMapping("/add")
    public String add(TodoDTO todo, @RequestParam(required = false) Integer parentId, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }

        todo.setMemberNo(loginUser.getMemberNo());
        if (parentId != null) {
            todo.setParentId(parentId);
        }
        todoService.save(todo);

        return "redirect:/todo/list";
    }

    @PostMapping("/update")
    @ResponseBody
    public String update(@RequestBody TodoDTO todo, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        TodoDTO existingTodo = todoService.findById(todo.getId());

        if (loginUser == null || existingTodo == null || existingTodo.getMemberNo() != loginUser.getMemberNo()) {
            return "error";
        }
        
        // Update all editable fields
        if (todo.getContent() != null) {
            existingTodo.setContent(todo.getContent());
        }
        if (todo.getDueDate() != null) {
            existingTodo.setDueDate(todo.getDueDate());
        }
        existingTodo.setCompleted(todo.isCompleted());
        
        todoService.update(existingTodo);

        return "success";
    }

    @GetMapping("/delete-completed")
    public String deleteCompleted(HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser != null) {
            todoService.deleteCompletedByMemberNo(loginUser.getMemberNo());
        }
        return "redirect:/todo/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        TodoDTO todo = todoService.findById(id);

        if (loginUser == null || todo == null || todo.getMemberNo() != loginUser.getMemberNo()) {
            // Or handle with an error message
            return "redirect:/todo/list";
        }

        todoService.deleteById(id);
        return "redirect:/todo/list";
    }
}
