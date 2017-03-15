package com.example.helloworld;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LetterController {
    @RequestMapping("/to/{who}")
    public String to(@PathVariable String who, @RequestParam String message) {
        return "To " + who + " " + message;
    }
}