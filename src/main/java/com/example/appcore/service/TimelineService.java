package com.example.appcore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.appcore.enums.TimelineVisibility;
import com.example.appcore.enums.TypeInteration;
import com.example.appcore.model.Student;
import org.springframework.stereotype.Service;
import com.example.appcore.model.Timeline;
import com.example.appcore.repository.TimelineRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TimelineService {

  private final TimelineRepository timelineRepository;

    public TimelineService(TimelineRepository repository) {
        this.timelineRepository = repository;
    }

    public List<Timeline> findAll() {
        return timelineRepository.findAll();
    }

    public Optional<Timeline> findById(Long id) {
        return timelineRepository.findById(id);
    }

    public Timeline create(Timeline timeline) {
        
        if (timeline.getTime() == null) {
            timeline.setTime(java.time.LocalDateTime.now());
        }
        return timelineRepository.save(timeline);
    }

    public Timeline update(Long id, Timeline timeline) {

        return timelineRepository.findById(id)
                .map(existing -> {
                    existing.setDescription(timeline.getDescription());
                    existing.setTime(timeline.getTime() != null ? timeline.getTime() : existing.getTime());
                    existing.setVisibility(timeline.getVisibility());
                    existing.setUser(timeline.getUser());
                    return timelineRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Timeline não encontrada: " + id));
                   
    }

    public void delete(Long id) {
        timelineRepository.deleteById(id);
    }

    public List<Timeline> findByUserId(Long userId) {
      return timelineRepository.findByUserId(userId);   
    }

    public boolean registrarInteracao(String tipoInteracao, Student usuario) {

        Optional<TypeInteration> interacao = TypeInteration.fromString(tipoInteracao);

        if (interacao.isEmpty()) {
            return false; // CT02
        }

        Timeline timeline = new Timeline();
        timeline.setUser(usuario);
        timeline.setDescription("Interação: " + interacao.get().name());
        timeline.setVisibility(TimelineVisibility.PUBLIC);
        timeline.setTime(LocalDateTime.now());
        timeline.setTypeInteration(interacao.get());

        timelineRepository.save(timeline);
        return true; // CT01
    }
    
}
