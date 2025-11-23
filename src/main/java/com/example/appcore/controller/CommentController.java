package com.example.appcore.controller;

import com.example.appcore.model.Comment;
import com.example.appcore.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    private Long parseLongSafely(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("O campo " + fieldName + " é obrigatório.");
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            }
            throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Erro de conversão: O campo " + fieldName + " deve ser um número inteiro válido.");
        }
    }

    private Double parseDoubleSafely(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("O campo " + fieldName + " é obrigatório.");
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
            throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Erro de conversão: O campo " + fieldName + " deve ser um número decimal válido (Ex: 4.5).");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateComment(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = parseLongSafely(payload.get("userId"), "userId");
            Long courseId = parseLongSafely(payload.get("courseId"), "courseId");
            Double rating = parseDoubleSafely(payload.get("rating"), "rating");
            String content = (String) payload.get("content");

            Comment updatedComment = commentService.updateExistingStudentComment(userId, courseId, rating, content);

            return ResponseEntity.ok(updatedComment); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erro interno ao atualizar comentário: " + e.getMessage()));
        }
    }


    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = parseLongSafely(payload.get("userId"), "userId");
            Long courseId = parseLongSafely(payload.get("courseId"), "courseId");
            Double rating = parseDoubleSafely(payload.get("rating"), "rating");
            String content = (String) payload.get("content");

            Comment newComment = commentService.createComment(userId, courseId, rating, content);

            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erro interno ao criar comentário: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}/professor/{professorId}")
    public ResponseEntity<List<Comment>> getCourseFeedbacks(@PathVariable Long courseId, @PathVariable Long professorId) {
        try {
            List<Comment> feedbacks = commentService.manageFeedbacks(courseId, professorId);
            return ResponseEntity.ok(feedbacks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{commentId}/reply")
    public ResponseEntity<?> replyToFeedback(@PathVariable Long commentId, @RequestBody Map<String, Object> payload) {
        try {
            Long professorId = parseLongSafely(payload.get("professorId"), "professorId");
            String reply = (String) payload.get("reply");

            boolean success = commentService.replyToFeedback(commentId, professorId, reply);

            if (success) {
                return ResponseEntity.ok(Map.of("message", "Resposta enviada com sucesso"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Falha ao processar resposta."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}")
    public List<Comment> listByCourse(@PathVariable Long courseId) {
        return commentService.listByCourse(courseId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getComment(id);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{commentId}/reply")
    public ResponseEntity<?> replyComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, Object> payload
    ) {
        Long userId = parseLongSafely(payload.get("userId"), "userId");
        String content = (String) payload.get("content");

        Comment reply = commentService.reply(commentId, userId, content);

        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }
}