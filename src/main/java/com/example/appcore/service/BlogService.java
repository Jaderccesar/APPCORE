package com.example.appcore.service;

import com.example.appcore.model.Blog;
import com.example.appcore.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public List<Blog> searchBlogs(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
          return new ArrayList<>();
        }

        List<Blog> byTitle = blogRepository.findByTitleContainingIgnoreCase(keyword);
        List<Blog> byContent = blogRepository.findByContentContainingIgnoreCase(keyword);

        List<Blog> result = new ArrayList<>();
        
        result.addAll(byTitle);

        for (Blog blog : byContent) {
            if (!result.contains(blog)) {
                result.add(blog);
            }
        }

        return result;
    }
}
