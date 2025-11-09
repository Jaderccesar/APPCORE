package com.example.appcore.appcore.service;

import com.example.appcore.appcore.enums.CourseStatus;
import com.example.appcore.appcore.model.Course;
import com.example.appcore.appcore.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) {
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setStatus(CourseStatus.DRAFT);
        return courseRepository.save(course);
    }

    public Course update(Long id, Course course) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + id));

        existing.setTitle(course.getTitle());
        existing.setDescription(course.getDescription());
        existing.setPrice(course.getPrice());
        existing.setImageUrl(course.getImageUrl());
        existing.setRating(course.getRating());
        existing.setLevel(course.getLevel());
        existing.setWorkload(course.getWorkload());
        existing.setCertificateEnabled(course.isCertificateEnabled());
        existing.setStatus(course.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        return courseRepository.save(existing);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }
}
