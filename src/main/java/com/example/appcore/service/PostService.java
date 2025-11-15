package com.example.appcore.service;

import com.example.appcore.model.Post;
import com.example.appcore.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Post update(Long id, Post post) {
        Post existing = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post com id " + id + " não encontrado"));

        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        existing.setCreateDate(post.getCreateDate());
        existing.setStatus(post.getStatus());

        return postRepository.save(existing);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }
}
