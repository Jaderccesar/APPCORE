package com.example.appcore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.appcore.model.Course;
import com.example.appcore.model.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long>{
  List<Video> findByCourse(Course course);
} 
