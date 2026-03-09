package com.example.blog.service;

import com.example.blog.dto.ScheduleDTO;
import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> findByMemberNoAndDateRange(int memberNo, String start, String end);
    void save(ScheduleDTO schedule);
    void update(ScheduleDTO schedule);
    void deleteById(int id);
    ScheduleDTO findById(int id);
}
