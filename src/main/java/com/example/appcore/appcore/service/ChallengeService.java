package com.example.appcore.appcore.service;

import com.example.appcore.appcore.enums.CorrectionType;
import com.example.appcore.appcore.model.Challenge;
import com.example.appcore.appcore.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    public List<Challenge> getChallenges() {
        return challengeRepository.findAll();
    }

    public Optional<Challenge> getChallengeById(Long id) {
        return challengeRepository.findById(id);
    }

    public Challenge save(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    public Challenge update(Long id, Challenge challenge) {
        Challenge existing = challengeRepository.findById(id).orElseThrow(() -> new RuntimeException("Desafio não encontrado com o id " + id));

        existing.setTitle(challenge.getTitle());
        existing.setDescription(challenge.getDescription());
        existing.setCorrectionType(challenge.getCorrectionType());
        existing.setStartDate(challenge.getStartDate());
        existing.setEndDate(challenge.getEndDate());

        return challengeRepository.save(existing);
    }

    public void delete(Long id){
        challengeRepository.deleteById(id);
    }
}
