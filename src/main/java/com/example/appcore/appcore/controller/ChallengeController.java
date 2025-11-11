package com.example.appcore.appcore.controller;

import com.example.appcore.appcore.model.Challenge;
import com.example.appcore.appcore.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping("/list")
    public List<Challenge> listAll() {
        return challengeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getById(@PathVariable Long id) {
        return challengeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    public List<Challenge> getByCourse(@PathVariable Long courseId) {
        return challengeService.findByCourseId(courseId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<Challenge> getByTeacher(@PathVariable Long teacherId) {
        return challengeService.findByTeacherId(teacherId);
    }

    @PostMapping("/save")
    public ResponseEntity<Challenge> save(@RequestBody Challenge challenge) {
        Challenge savedChallenge = challengeService.save(challenge);
        return ResponseEntity.ok(savedChallenge);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Challenge> update(@PathVariable Long id, @RequestBody Challenge challenge) {
        Challenge updatedChallenge = challengeService.update(id, challenge);
        return ResponseEntity.ok(updatedChallenge);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        challengeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
