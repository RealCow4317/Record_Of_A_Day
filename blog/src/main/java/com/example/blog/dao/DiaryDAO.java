package com.example.blog.dao;

import com.example.blog.dto.DiaryDTO;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

public interface DiaryDAO {
    void save(DiaryDTO diary);

    List<DiaryDTO> findByMemberNoAndDateRange(@Param("memberNo") int memberNo, @Param("startDate") String startDate, @Param("endDate") String endDate);

    DiaryDTO findByMemberNoAndDiaryDate(@Param("memberNo") int memberNo, @Param("diaryDate") Date diaryDate);

    void update(DiaryDTO diary);
}
