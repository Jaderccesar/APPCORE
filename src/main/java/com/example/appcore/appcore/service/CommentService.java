package com.example.appcore.appcore.service;

import com.example.appcore.appcore.model.Comment;
import com.example.appcore.appcore.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

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
}
