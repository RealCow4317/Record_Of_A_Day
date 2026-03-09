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

    @Autowired
    private MailService mailService;

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

    @Override
    public void findPassword(String id, String email) {
        MemberDTO member = memberDAO.getMemberById(id);
        
        if (member == null || !member.getEmail().equals(email)) {
            throw new RuntimeException("일치하는 회원 정보가 없습니다.");
        }

        // 임시 비밀번호 생성 (8자리)
        String temporaryPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        
        // 비밀번호 업데이트 (BCrypt 암호화는 updateMember 내부에서 처리됨)
        member.setPassword(temporaryPassword);
        updateMember(member);

        // 메일 발송
        mailService.sendTemporaryPassword(email, temporaryPassword);
    }
}
