package com.example.appcore.repository;

import com.example.appcore.model.Course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

  @Override
  @EntityGraph(attributePaths = {"teacher", "videos", "comments", "certificates", "promotions", "challenges"})
  Optional<Course> findById(Long id);
  List<Course> findByTeacherId(Long teacherId);
  
}
