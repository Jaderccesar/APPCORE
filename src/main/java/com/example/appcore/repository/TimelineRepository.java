package com.example.appcore.repository;

import com.example.appcore.model.Timeline;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long> {
  List<Timeline> findByUserId(Long userId);
  List<Timeline> findByUserIdOrderByTimeDesc(Long userId);
}
