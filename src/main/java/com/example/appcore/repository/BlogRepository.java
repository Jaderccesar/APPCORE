package com.example.appcore.repository;

import com.example.appcore.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findByTitleContainingIgnoreCase(String keyword);

    List<Blog> findByContentContainingIgnoreCase(String keyword);
}