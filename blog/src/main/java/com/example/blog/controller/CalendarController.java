package com.example.blog.controller;

import com.example.blog.dto.DiaryDTO;
import com.example.blog.dto.MemberDTO;
import com.example.blog.dto.ScheduleDTO;
import com.example.blog.dto.TodoDTO;
import com.example.blog.service.DiaryService;
import com.example.blog.service.HolidayService;
import com.example.blog.service.ScheduleService;
import com.example.blog.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
@Slf4j
public class CalendarController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private ScheduleService scheduleService;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/view")
    public String calendarView(HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            log.warn("Unauthorized access attempt to calendar view");
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

        log.info("Fetching calendar events from {} to {} for member: {}", start, end, loginUser.getMemberNo());

        String startDate = (start.length() >= 10) ? start.substring(0, 10) : start;
        String endDate = (end.length() >= 10) ? end.substring(0, 10) : end;

        List<Map<String, Object>> events = new ArrayList<>();

        // 1. 다이어리 데이터 추가
        List<DiaryDTO> diaryEntries = diaryService.findByMemberNoAndDateRange(loginUser.getMemberNo(), startDate, endDate);
        for (DiaryDTO entry : diaryEntries) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", "diary_" + entry.getId());
            event.put("title", entry.getContent());
            event.put("start", entry.getDiaryDate().toString());
            event.put("allDay", true);
            event.put("displayOrder", 1);
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
            String title = (todo.isCompleted() ? "✅ " : "⏳ ") + todo.getContent();
            event.put("title", title);
            event.put("start", todo.getDueDate().toString());
            event.put("allDay", true);
            event.put("className", "todo-event");
            event.put("displayOrder", 2);
            
            Map<String, Object> extendedProps = new HashMap<>();
            extendedProps.put("type", "todo");
            extendedProps.put("completed", todo.isCompleted());
            event.put("extendedProps", extendedProps);
            
            if (todo.isCompleted()) {
                event.put("backgroundColor", "#e9ecef");
                event.put("borderColor", "#dee2e6");
                event.put("textColor", "#adb5bd");
            } else {
                event.put("backgroundColor", "#ffffff"); // 배경도 흰색 계열로 변경
                event.put("borderColor", "#dee2e6");
                event.put("textColor", "#000000"); // 갈색에서 검정색으로 변경
            }
            events.add(event);
        }

        // 3. 일반 일정(Schedule) 데이터 추가
        List<ScheduleDTO> schedules = scheduleService.findByMemberNoAndDateRange(loginUser.getMemberNo(), startDate, endDate);
        for (ScheduleDTO schedule : schedules) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", "schedule_" + schedule.getId());
            event.put("title", "📅 " + schedule.getTitle());
            
            if (schedule.getStartDate() != null) {
                event.put("start", dateFormat.format(schedule.getStartDate()));
            }
            
            if (schedule.getEndDate() != null) {
                // FullCalendar의 end 날짜는 해당 날짜를 포함하지 않으므로 +1일을 해줌
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(schedule.getEndDate());
                c.add(java.util.Calendar.DATE, 1);
                event.put("end", dateFormat.format(c.getTime()));
            }
            
            event.put("allDay", true);
            event.put("backgroundColor", schedule.getColor());
            event.put("borderColor", schedule.getColor());
            event.put("className", "schedule-event");
            event.put("displayOrder", 3);

            Map<String, Object> extendedProps = new HashMap<>();
            extendedProps.put("type", "schedule");
            extendedProps.put("content", schedule.getContent());
            event.put("extendedProps", extendedProps);
            events.add(event);
        }

        return events;
    }

    @PostMapping("/schedule")
    @ResponseBody
    public ResponseEntity<String> saveSchedule(ScheduleDTO schedule, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("Unauthorized");

        schedule.setMemberNo(loginUser.getMemberNo());
        
        if (schedule.getId() != null && schedule.getId() > 0) {
            log.info("Updating existing schedule: {}", schedule.getId());
            scheduleService.update(schedule);
        } else {
            log.info("Saving new schedule");
            scheduleService.save(schedule);
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/delete-schedule")
    @ResponseBody
    public ResponseEntity<String> deleteSchedule(@RequestParam int id, HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("Unauthorized");

        scheduleService.deleteById(id);
        return ResponseEntity.ok("Success");
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
            log.info("Diary saved for user: {}, date: {}", loginUser.getMemberNo(), diary.getDiaryDate());
        } catch (Exception e) {
            log.error("Failed to save diary", e);
        }

        return "redirect:/calendar/view";
    }

    @PostMapping("/delete-diary")
    @ResponseBody
    public ResponseEntity<String> deleteDiary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.util.Date utilDate,
                                              HttpSession session) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            log.warn("Unauthorized diary delete attempt");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        log.info("Request to delete diary for date: {} by member: {}", sqlDate, loginUser.getMemberNo());

        try {
            diaryService.deleteDiary(loginUser.getMemberNo(), sqlDate);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            log.error("Failed to delete diary for date: {}", sqlDate, e);
            return ResponseEntity.status(500).body("Error");
        }
    }

    @GetMapping("/holidays")
    @ResponseBody
    public List<Map<String, Object>> getHolidays(@RequestParam String start, @RequestParam String end) {
        return holidayService.getHolidays(start, end);
    }
}
