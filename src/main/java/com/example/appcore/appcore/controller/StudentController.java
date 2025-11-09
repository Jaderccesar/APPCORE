package com.example.appcore.appcore.controller;

import com.example.appcore.appcore.model.Student;
import com.example.appcore.appcore.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/Students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/List")
    public List<Student> list() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public Student listById(@PathVariable Long id) {
        return studentService.findById(id).orElse(null);
    }

    @PostMapping("/save")
    public Student save(@RequestBody Student student) {
        return studentService.save(student);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        if (student == null) {
            return ResponseEntity.badRequest().build();
        }

        Student updatedStudent = studentService.update(id, student);
         return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
