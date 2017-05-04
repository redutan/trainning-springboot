package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @RequestMapping("/")
    public String main() {
        return "main";
    }

    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }
}
