package com.example.appcore.appcore.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.appcore.appcore.enums.RankType;
import com.example.appcore.appcore.model.Ranking;
import com.example.appcore.appcore.repository.RankingRepository;

public class RankingService {

  private final RankingRepository rankingRepository;

    public RankingService(RankingRepository repository) {
        this.rankingRepository = repository;
    }

    public List<Ranking> findAll() {
        return rankingRepository.findAll();
    }

    public Optional<Ranking> findById(Long id) {
        return rankingRepository.findById(id);
    }

    public Ranking create(Ranking ranking) {
        if (ranking.getUpdateDate() == null) {
            ranking.setUpdateDate(LocalDate.now());
        }
        return rankingRepository.save(ranking);
    }

    public Ranking update(Long id, Ranking updated) {
        return rankingRepository.findById(id)
                .map(existing -> {
                    existing.setType(updated.getType());
                    existing.setUpdateDate(updated.getUpdateDate() != null ? updated.getUpdateDate() : existing.getUpdateDate());
                    existing.setParticipants(updated.getParticipants());
                    return rankingRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Ranking não encontrado: " + id));
    }
 
    public void delete(Long id) {
        rankingRepository.deleteById(id);
    }

    public List<Ranking> findByType(RankType type) {
      return rankingRepository.findByType(type);
    }
    
}
