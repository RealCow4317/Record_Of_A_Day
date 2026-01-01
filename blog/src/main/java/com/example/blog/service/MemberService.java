package com.example.blog.service;

import com.example.blog.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    void register(MemberDTO member);
    MemberDTO login(MemberDTO member);
    MemberDTO getMember(String id);
    List<MemberDTO> getAllMembers();
    void deleteMember(int memberNo);
    void updateMember(MemberDTO member);
    List<MemberDTO> searchMembers(String keyword);
    MemberDTO getMemberByNo(int memberNo);
    MemberDTO getMemberByEmail(String email);
}
