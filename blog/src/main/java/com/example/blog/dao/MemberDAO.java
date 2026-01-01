package com.example.blog.dao;

import com.example.blog.dto.MemberDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberDAO {
    void insertMember(MemberDTO member);           // 회원가입
    MemberDTO loginMember(MemberDTO member);       // 로그인
    MemberDTO getMemberById(String id);            // ID로 조회 (마이페이지 등)
    List<MemberDTO> getAllMembers();
    void deleteMember(int memberNo);
    void updateMember(MemberDTO member);
    List<MemberDTO> searchMembers(@Param("keyword") String keyword);
    MemberDTO getMemberByNo(int memberNo);
    MemberDTO getMemberByEmail(String email);
}
