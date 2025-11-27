package com.example.appcore.repository;

import com.example.appcore.model.ChallengeResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeResultRepository extends JpaRepository<ChallengeResult, Long> {

    // Conta quantos desafios finalizados o aluno tem
    long countByStudentIdAndCompletedTrue(Long studentId);

}
