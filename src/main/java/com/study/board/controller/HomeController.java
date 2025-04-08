package com.study.board.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("board/index")
    public String index() {
        return "index"; // 템플릿 폴더의 index.html을 찾아간다~
    }
}
