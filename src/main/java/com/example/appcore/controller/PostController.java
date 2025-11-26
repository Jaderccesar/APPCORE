package com.example.appcore.controller;

import com.example.appcore.model.Post;
import com.example.appcore.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> findAll() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> findById(@PathVariable Long id) {
        return postService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> save(@RequestBody Post post) {
        return ResponseEntity.ok(postService.save(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post post) {
        try {
            Post updated = postService.update(id, post);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            postService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
