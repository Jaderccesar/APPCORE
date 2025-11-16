package com.example.appcore.service;

import com.example.appcore.model.Comment;
import com.example.appcore.repository.CommentRepository;
import com.example.appcore.repository.CourseRepository;
import com.example.appcore.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getComment(Long id) {
        return commentRepository.findById(id);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment update(Long id, Comment comment) {
        Comment existing = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comentário com o id" + id + " não encontrado"));

        existing.setContent(comment.getContent());
        existing.setAuthorName(comment.getAuthorName());
        existing.setRating(comment.getRating());

        return commentRepository.save(existing);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    
    // CT02 — Listar feedbacks por curso e professor
    public List<Comment> manageFeedbacks(Long courseId, Long professorId) {

        if (courseId == null || professorId == null) {
            throw new IllegalArgumentException("IDs inválidos");
        }

        // curso precisa existir
        courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        // professor precisa existir
        userRepository.findById(professorId) 
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // buscar comentários do curso
        return commentRepository.findByCourseId(courseId);
    }

    public boolean validateReply(String reply) {

        if (reply == null || reply.trim().isEmpty()) {
        throw new IllegalArgumentException("Campo de resposta obrigatório");
         }
        return true;
    }

    // Publicar resposta para um feedback específico
    public boolean replyToFeedback(Long commentId, Long professorId, String reply) {

        validateReply(reply);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        comment.setResponse(reply);
        comment.setRespondedBy(professorId);

        commentRepository.save(comment);
        return true;
    }
}
