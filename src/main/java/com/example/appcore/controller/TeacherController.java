package com.example.appcore.controller;

import com.example.appcore.model.Teacher;
import com.example.appcore.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/Teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> listById(@PathVariable Long id) {

        return teacherService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Teacher> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        if (teacher == null) {
            return ResponseEntity.badRequest().build();
        }

        Teacher updatedTeacher = teacherService.update(id, teacher);
        return ResponseEntity.ok(updatedTeacher);
    }

}