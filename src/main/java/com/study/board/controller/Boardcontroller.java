package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.jwt.JwtUtil;
import com.study.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class Boardcontroller {

    private final BoardService boardService;
    private final JwtUtil jwtUtil;

    @GetMapping("/board/write")
    public String boardWriteForm() {
        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String writePro(@ModelAttribute Board board,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String loginEmail = extractEmailFromToken(authHeader);
        boardService.write(board, loginEmail);
        return "redirect:/board/list";
    }

    @GetMapping("/board/list")
    public String boardList(Model model) {
        model.addAttribute("list", boardService.boardList());
        return "boardlist";
    }

    @GetMapping("/board/view")
    public String boardView(Model model, Integer id) {
        model.addAttribute("board", boardService.boardview(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardview(id));
        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(
            @PathVariable("id") Integer id,
            @RequestBody Board board,  // 수정
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        Board boardTemp = boardService.boardview(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        String loginEmail = extractEmailFromToken(authHeader);
        boardService.write(boardTemp, loginEmail);
        return "redirect:/board/list";  // JS에서 처리하므로 이건 사실 무시됨
    }


    // 토큰에서 이메일 추출 (Bearer 제거 포함)
    private String extractEmailFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getEmail(token);
        } else {
            throw new RuntimeException("유효하지 않은 인증 토큰입니다.");
        }
    }
}
