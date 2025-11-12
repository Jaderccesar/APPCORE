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
    public List<Challenge> list() {
        return challengeService.getChallenges();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getById(@PathVariable Long id) {
        return challengeService.getChallengeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Challenge challenge) {
        try {
            Challenge saved = challengeService.save(challenge);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Erro ao salvar desafio: " + ex.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Challenge challenge) {
        try {
            Challenge updated = challengeService.update(id, challenge);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(404).body("Erro ao atualizar desafio: " + ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            challengeService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Erro ao deletar: " + ex.getMessage());
        }
    }
}
