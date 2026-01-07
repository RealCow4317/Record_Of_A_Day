package com.example.blog.controller;

import com.example.blog.dto.DiaryDTO;
import com.example.blog.dto.MemberDTO;
import com.example.blog.service.DiaryService;
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

        List<DiaryDTO> diaryEntries = diaryService.findByMemberNoAndDateRange(loginUser.getMemberNo(), start, end);
        List<Map<String, Object>> events = new ArrayList<>();

        for (DiaryDTO entry : diaryEntries) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", entry.getId());
            event.put("title", entry.getContent());
            event.put("start", entry.getDiaryDate().toString());
            event.put("allDay", true);
            // Add extendedProps for imagePath
            Map<String, Object> extendedProps = new HashMap<>();
            // Use thumbnailPath for calendar view if available, otherwise fallback to original imagePath
            String displayImage = (entry.getThumbnailPath() != null) ? entry.getThumbnailPath() : entry.getImagePath();
            extendedProps.put("imagePath", displayImage);
            event.put("extendedProps", extendedProps);
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
}
