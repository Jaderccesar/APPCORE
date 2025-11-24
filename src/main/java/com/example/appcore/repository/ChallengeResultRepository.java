package com.example.appcore.repository;

import com.example.appcore.model.ChallengeResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeResultRepository extends JpaRepository<ChallengeResult, Long> {

    long countByStudentIdAndCompletedTrue(Long studentId);
}
