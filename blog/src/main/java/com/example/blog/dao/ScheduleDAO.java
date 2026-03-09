package com.example.blog.dao;

import com.example.blog.dto.ScheduleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleDAO {
    List<ScheduleDTO> findByMemberNoAndDateRange(@Param("memberNo") int memberNo, @Param("start") String start, @Param("end") String end);
    void save(ScheduleDTO schedule);
    void update(ScheduleDTO schedule);
    void deleteById(int id);
    ScheduleDTO findById(int id);
}
