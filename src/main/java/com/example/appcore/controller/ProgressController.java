package com.example.appcore.controller;

import com.example.appcore.model.ChallengeResult;
import com.example.appcore.model.Enrollment;
import com.example.appcore.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/enroll/{studentId}/{courseId}")
    public ResponseEntity<?> enroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            Enrollment e = progressService.enrollStudent(studentId, courseId);
            return ResponseEntity.ok(e);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    @PostMapping("/complete-video")
    public ResponseEntity<?> completeVideo(@RequestBody Map<String, Object> body) {
        try {
            Long studentId = Long.valueOf(body.get("studentId").toString());
            Long courseId = Long.valueOf(body.get("courseId").toString());
            Long videoId = Long.valueOf(body.get("videoId").toString());
            Map<String, Object> res = progressService.completeVideo(studentId, courseId, videoId);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    @PostMapping("/complete-challenge")
    public ResponseEntity<?> completeChallenge(@RequestBody Map<String, Object> body) {
        try {
            Long studentId = Long.valueOf(body.get("studentId").toString());
            Long challengeId = Long.valueOf(body.get("challengeId").toString());
            Integer score = Integer.valueOf(body.get("score").toString());
            ChallengeResult cr = progressService.completeChallenge(studentId, challengeId, score);
            return ResponseEntity.ok(cr);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getProgress(@PathVariable Long studentId) {
        try {
            Map<String, Object> res = progressService.getStudentProgress(studentId);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }
}