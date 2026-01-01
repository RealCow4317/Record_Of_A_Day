package com.example.blog.service;

import com.example.blog.dao.MemberDAO;
import com.example.blog.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(MemberDTO member) {
        if (memberDAO.getMemberById(member.getId()) != null) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        if (memberDAO.getMemberByEmail(member.getEmail()) != null) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }
        memberDAO.insertMember(member);
    }

    @Override
    public MemberDTO login(MemberDTO member) {
        MemberDTO storedMember = memberDAO.getMemberById(member.getId());
        if (storedMember != null && passwordEncoder.matches(member.getPassword(), storedMember.getPassword())) {
            return storedMember;
        }
        return null;
    }

    @Override
    public MemberDTO getMember(String id) {
        return memberDAO.getMemberById(id);
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        return memberDAO.getAllMembers();
    }

    @Override
    public void deleteMember(int memberNo) {
        memberDAO.deleteMember(memberNo);
    }

    @Override
    public void updateMember(MemberDTO member) {
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        }
        memberDAO.updateMember(member);
    }

    @Override
    public List<MemberDTO> searchMembers(String keyword) {
        return memberDAO.searchMembers(keyword);
    }

    @Override
    public MemberDTO getMemberByNo(int memberNo) {
        return memberDAO.getMemberByNo(memberNo);
    }

    @Override
    public MemberDTO getMemberByEmail(String email) {
        return memberDAO.getMemberByEmail(email);
    }
}
