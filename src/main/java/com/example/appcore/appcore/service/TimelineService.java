package com.example.appcore.appcore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.appcore.appcore.model.Timeline;
import com.example.appcore.appcore.repository.TimelineRepository;

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
    
}
