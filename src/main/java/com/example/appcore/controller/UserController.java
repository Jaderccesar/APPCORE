package com.example.appcore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.appcore.model.User;
import com.example.appcore.service.UserService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
      User created = userService.createUserFromMap(body);
      return ResponseEntity.ok(created);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password) {

        try {
            Object user = userService.login(email, password);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    @PostMapping("/uploadAvatar/{userId}/{accountType}")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long userId,
            @PathVariable String accountType,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "O arquivo está vazio."));
        }

        try {

            String fileUrl = userService.saveAvatarAndReturnUrl(userId, file, accountType.toUpperCase());

            return ResponseEntity.ok(Map.of("avatarUrl", fileUrl, "message", "Avatar atualizado com sucesso."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro no upload do avatar: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao salvar o arquivo: " + e.getMessage()));
        }
    }
}
