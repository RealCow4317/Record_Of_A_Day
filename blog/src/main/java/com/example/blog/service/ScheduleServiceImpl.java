package com.example.blog.service;

import com.example.blog.dao.ScheduleDAO;
import com.example.blog.dto.ScheduleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDAO scheduleDAO;

    @Override
    public List<ScheduleDTO> findByMemberNoAndDateRange(int memberNo, String start, String end) {
        log.info("Fetching schedules from {} to {} for member: {}", start, end, memberNo);
        return scheduleDAO.findByMemberNoAndDateRange(memberNo, start, end);
    }

    @Override
    public void save(ScheduleDTO schedule) {
        log.info("Saving new schedule: {}", schedule.getTitle());
        scheduleDAO.save(schedule);
    }

    @Override
    public void update(ScheduleDTO schedule) {
        log.info("Updating schedule id: {}", schedule.getId());
        scheduleDAO.update(schedule);
    }

    @Override
    public void deleteById(int id) {
        log.info("Deleting schedule id: {}", id);
        scheduleDAO.deleteById(id);
    }

    @Override
    public ScheduleDTO findById(int id) {
        return scheduleDAO.findById(id);
    }
}
