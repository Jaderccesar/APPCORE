package com.example.appcore.service;

import com.example.appcore.enums.AccountType;
import com.example.appcore.model.Comment;
import com.example.appcore.model.Course;
import com.example.appcore.model.User;
import com.example.appcore.repository.CommentRepository;
import com.example.appcore.repository.CourseRepository;
import com.example.appcore.repository.UserRepository;
import com.fasterxml.jackson.databind.node.DoubleNode;
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

    private void isUserAuthorizedToComment(Long userId, Long courseId) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Autor (User ID) não encontrado."));

        if (author.getAccountType() == AccountType.TEACHER) {
            throw new IllegalArgumentException("Professores não estão autorizados a comentar ou avaliar cursos.");
        }
    }

    public Comment addComment(Comment comment) {

        Long userId = comment.getAuthor().getId();
        Long courseId = comment.getCourse().getId();
        Double rating = comment.getRating();

        isUserAuthorizedToComment(userId, courseId);

        Optional<Comment> existingRating =
                commentRepository.findByAuthorIdAndCourseIdAndRatingNotNull(userId, courseId);

        if (existingRating.isPresent()) {
            comment.setRating(null);
        }

        return commentRepository.save(comment);
    }


    public List<Comment> listByCourse(Long courseId) {
        return commentRepository.findByCourseIdAndParentIsNull(courseId);
    }

    public Comment reply(Long parentId, Long userId, String content) {

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Comentário pai não encontrado"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comment reply = new Comment();
        reply.setContent(content);
        reply.setAuthor(author);
        reply.setAuthorName(author.getName());
        reply.setCourse(parent.getCourse());
        reply.setParent(parent); // 🔥 encadeamento

        parent.getReplies().add(reply);

        return commentRepository.save(reply);
    }

    public Comment createComment(Long userId, Long courseId, Double rating, String content) {

        isUserAuthorizedToComment(userId, courseId);

        if (!validarComentario(content)) {
            throw new IllegalArgumentException("O conteúdo do comentário é inválido (vazio, muito longo ou contém caracteres perigosos).");
        }
        if (rating == null || rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("A avaliação (rating) deve ser entre 1.0 e 5.0 estrelas.");
        }

        User author = userRepository.findById(userId).get();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));

        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setRating(rating);
        newComment.setCourse(course);
        newComment.setAuthor(author);
        newComment.setAuthorName(author.getName());

        return commentRepository.save(newComment);
    }

    private void checkCreationAuthorization(Long userId, Long courseId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Autor (User ID) não encontrado."));

        if (author.getAccountType().equals("TEACHER")) {
            throw new IllegalArgumentException("Professores não estão autorizados a comentar ou avaliar cursos.");
        }

        if (commentRepository.findByAuthorIdAndCourseId(userId, courseId).isPresent()) {
            throw new IllegalArgumentException("Você já possui um comentário/avaliação para este curso. Por favor, use a opção de modificação.");
        }
    }

    public Comment updateExistingStudentComment(Long userId, Long courseId, Double newRating, String newContent) {
        Comment existingComment = commentRepository.findByAuthorIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada para este curso e usuário."));

        if (!validarComentario(newContent)) {
            throw new IllegalArgumentException("O conteúdo do comentário é inválido.");
        }
        if (newRating == null || newRating < 1.0 || newRating > 5.0) {
            throw new IllegalArgumentException("A avaliação (rating) deve ser entre 1.0 e 5.0 estrelas.");
        }

        existingComment.setContent(newContent);
        existingComment.setRating(newRating);

        //Alterei aqui leandro

        return commentRepository.save(existingComment);
    }

    // CT02 — Listar feedbacks por curso e professor
    public List<Comment> manageFeedbacks(Long courseId, Long professorId) {

        if (courseId == null || professorId == null) {
            throw new IllegalArgumentException("IDs inválidos");
        }

        courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        userRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        return commentRepository.findByCourseId(courseId);
    }

    public boolean validateReply(String reply) {

        if (reply == null || reply.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo de resposta obrigatório");
        }
        return true;
    }

    public boolean replyToFeedback(Long commentId, Long professorId, String reply) {

        validateReply(reply);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        comment.setResponse(reply);
        comment.setRespondedBy(professorId);

        commentRepository.save(comment);
        return true;
    }

    public boolean validarComentario(String comentario) {

        if (comentario == null) return false;

        String texto = comentario.trim();

        if (texto.isEmpty()) return false;
        if (texto.length() > 500) return false;
        if (texto.matches(".*<[^>]+>.*")) return false;

        return true;
    }
}