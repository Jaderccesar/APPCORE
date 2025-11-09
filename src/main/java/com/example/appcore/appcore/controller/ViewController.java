package com.example.appcore.appcore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/register")
    public String showRegisterPage() {
        return "user-register";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/register";
    }
}
