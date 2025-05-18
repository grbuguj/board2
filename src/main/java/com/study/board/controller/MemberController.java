package com.study.board.controller;
import com.study.board.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.study.board.dto.MemberDTO;
import com.study.board.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor

public class MemberController {
    // 생성자 주입
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    //

    @GetMapping("/board/save")
    public String saveForm(Principal principal) {
        return "save";
    }


    @PostMapping("/board/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        System.out.println("MemberController.save");
        System.out.println("memberDTO =" + memberDTO);
        memberService.save(memberDTO);
        return "login";
    }

    @GetMapping("/board/login")
    public String loginForm()  {
        return "login";
    }

    @PostMapping("/board/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody MemberDTO memberDTO) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            String token = jwtUtil.createToken(loginResult.getMemberEmail());
            return ResponseEntity.ok(token); // 토큰만 리턴
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    @GetMapping("/member/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면~ model을 사용
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    @GetMapping("/member/delete/{id}")
    public String deleteByID(@PathVariable long id) {
        memberService.deleteByID(id);
        return "redirect:/member/";
    }

}