package com.example.appcore.appcore.controller;

import com.example.appcore.appcore.model.Student;
import com.example.appcore.appcore.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ViewController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "user-register";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session, @RequestParam(required = false) Long id) {

        Long userId = null;

        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId != null) {
            userId = Long.parseLong(sessionUserId.toString());
        }

        if (userId == null && id != null) {
            userId = id;
            session.setAttribute("userId", userId);
        }
        
        if (userId != null) {
            Optional<Student> student = studentService.findById(userId);
            if (student.isPresent()) {
                model.addAttribute("user", student.get());
                model.addAttribute("userId", userId);
            }
        }
        
        return "profile";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/register";
    }
}
