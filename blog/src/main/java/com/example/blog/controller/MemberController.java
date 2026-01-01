package com.example.blog.controller;

import com.example.blog.dto.MemberDTO;
import com.example.blog.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입 폼
    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("member", new MemberDTO());
        return "member/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("member") MemberDTO member,
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "member/join";
        }

        try {
            memberService.register(member);
            redirectAttributes.addFlashAttribute("welcomeMessage", member.getId() + "님, 환영합니다!");
            return "redirect:/member/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/member/join";
        }
    }

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(MemberDTO input, HttpSession session, Model model) {
        MemberDTO dbMember = memberService.getMember(input.getId());

        if (dbMember != null && passwordEncoder.matches(input.getPassword(), dbMember.getPassword())) {
            session.setAttribute("loginUser", dbMember);
            session.setAttribute("isAdmin", dbMember.isAdmin());
            String redirect = (String) session.getAttribute("redirectAfterLogin");

            session.removeAttribute("redirectAfterLogin");

            return "redirect:" + (redirect != null ? redirect : "/");
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            model.addAttribute("enteredId", input.getId());
            return "member/login";
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 프로필
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("loginUser", loginUser);
        return "member/profile";
    }

    @GetMapping(value = "/check-id", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String checkId(@RequestParam String id) {
        return memberService.getMember(id) == null ? "true" : "false";
    }

    @GetMapping(value = "/check-email", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String checkEmail(@RequestParam String email) {
        return memberService.getMemberByEmail(email) == null ? "true" : "false";
    }

    // 비밀번호 변경 폼
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session, Model model) {
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("loginUser", loginUser);
        return "member/changePassword";
    }

    // 비밀번호 변경 처리
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/member/login";
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, loginUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 올바르지 않습니다.");
            return "redirect:/member/change-password";
        }

        // 새 비밀번호 암호화 후 저장
        loginUser.setPassword(newPassword);
        memberService.updateMember(loginUser);

        // 로그아웃 처리
        session.invalidate();

        // 리다이렉트 시 메시지 전달
        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
        return "redirect:/member/login";

    }
}
