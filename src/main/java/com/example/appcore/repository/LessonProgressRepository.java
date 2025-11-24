package com.example.appcore.repository;

import com.example.appcore.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    long countByEnrollmentIdAndCompletedTrue(Long enrollmentId);

    boolean existsByEnrollmentIdAndVideoIdAndCompletedTrue(Long enrollmentId, Long videoId);

    long countByEnrollmentIdAndVideoId(Long enrollmentId, Long videoId);
}
