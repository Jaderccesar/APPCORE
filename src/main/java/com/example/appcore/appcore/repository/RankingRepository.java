package com.example.appcore.appcore.repository;

import com.example.appcore.appcore.enums.RankType;
import com.example.appcore.appcore.model.Ranking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
  List<Ranking> findByType(RankType type);
}
