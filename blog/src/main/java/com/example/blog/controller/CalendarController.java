package com.example.blog.controller;

import com.example.blog.dto.DiaryDTO;
import com.example.blog.dto.MemberDTO;
import com.example.blog.dto.TodoDTO;
import com.example.blog.service.DiaryService;
import com.example.blog.service.HolidayService;
import com.example.blog.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private TodoService todoService;

    @GetMapping("/view")
    public String calendarView(HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }
        return "calendar/view";
    }

    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getDiaryEvents(@RequestParam String start, @RequestParam String end, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return new ArrayList<>();
        }

        // ISO 문자열에서 날짜 부분(YYYY-MM-DD)만 추출
        String startDate = start.substring(0, 10);
        String endDate = end.substring(0, 10);

        List<Map<String, Object>> events = new ArrayList<>();

        // 1. 다이어리 데이터 추가
        List<DiaryDTO> diaryEntries = diaryService.findByMemberNoAndDateRange(loginUser.getMemberNo(), startDate, endDate);
        for (DiaryDTO entry : diaryEntries) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", "diary_" + entry.getId());
            event.put("title", entry.getContent());
            event.put("start", entry.getDiaryDate().toString());
            event.put("allDay", true);
            Map<String, Object> extendedProps = new HashMap<>();
            String displayImage = (entry.getThumbnailPath() != null) ? entry.getThumbnailPath() : entry.getImagePath();
            extendedProps.put("imagePath", displayImage);
            event.put("extendedProps", extendedProps);
            events.add(event);
        }

        // 2. TODO 데이터 추가
        List<TodoDTO> todos = todoService.findByMemberNoAndDateRange(loginUser.getMemberNo(), startDate, endDate);
        for (TodoDTO todo : todos) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", "todo_" + todo.getId());
            
            // 완료 여부에 따라 제목 표시 변경
            String title = (todo.isCompleted() ? "✅ " : "⏳ ") + todo.getContent();
            event.put("title", title);
            event.put("start", todo.getDueDate().toString());
            event.put("allDay", true);
            event.put("className", "todo-event");
            
            Map<String, Object> extendedProps = new HashMap<>();
            extendedProps.put("type", "todo");
            extendedProps.put("completed", todo.isCompleted());
            event.put("extendedProps", extendedProps);
            
            // 할 일은 배경색 등으로 구분
            if (todo.isCompleted()) {
                event.put("backgroundColor", "#e9ecef");
                event.put("borderColor", "#dee2e6");
                event.put("textColor", "#adb5bd");
            } else {
                event.put("backgroundColor", "#fff3cd");
                event.put("borderColor", "#ffeeba");
                event.put("textColor", "#856404");
            }
            
            events.add(event);
        }

        return events;
    }

    @GetMapping("/diary-entry")
    @ResponseBody
    public DiaryDTO getDiaryEntry(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.util.Date utilDate,
                                  HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return null;
        }
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        return diaryService.findByMemberNoAndDiaryDate(loginUser.getMemberNo(), sqlDate);
    }

    @PostMapping("/diary")
    public String addDiaryEntry(@RequestParam("diaryDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.util.Date utilDate,
                                @RequestParam("content") String content,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }

        try {
            DiaryDTO diary = new DiaryDTO();
            diary.setMemberNo(loginUser.getMemberNo());
            diary.setDiaryDate(new java.sql.Date(utilDate.getTime()));
            diary.setContent(content);

            diaryService.saveDiary(diary, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/calendar/view";
    }

    @GetMapping("/holidays")
    @ResponseBody
    public List<Map<String, Object>> getHolidays(@RequestParam String start, @RequestParam String end) {
        return holidayService.getHolidays(start, end);
    }
}
