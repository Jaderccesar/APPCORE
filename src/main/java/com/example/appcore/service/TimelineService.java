package com.example.appcore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.appcore.enums.TimelineVisibility;
import com.example.appcore.enums.TypeInteration;
import com.example.appcore.model.Student;
import com.example.appcore.repository.StudentRepository;
import org.springframework.stereotype.Service;
import com.example.appcore.model.Timeline;
import com.example.appcore.repository.TimelineRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TimelineService {

  private final TimelineRepository timelineRepository;
  private final StudentRepository studentRepository;

    public TimelineService(TimelineRepository timelineRepository, StudentRepository studentRepository) {
        this.timelineRepository = timelineRepository;
        this.studentRepository = studentRepository;
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

    public Timeline registrarInteracao(Long userId, TypeInteration tipo, Student usuario) {

        if (usuario == null || !usuario.getId().equals(userId)) {
            throw new IllegalArgumentException("Usuário inválido.");
        }

        Timeline timeline = new Timeline();
        timeline.setUser(usuario);
        timeline.setDescription(gerarDescricao(tipo));
        timeline.setTime(LocalDateTime.now());
        timeline.setVisibility(TimelineVisibility.PUBLIC);
        timeline.setTypeInteration(tipo);

        return timelineRepository.save(timeline);
    }
    private String gerarDescricao(TypeInteration tipo) {
        return switch (tipo) {
            case COMMENT -> "Usuário fez um comentário";
            case LIKE -> "Usuário curtiu uma publicação";
            case FOLLOWED -> "Usuário seguiu outro usuário";
            case POST -> "Usuário publicou um novo post";
            case CHALLENGE_COMPLETED -> "Usuário concluiu um desafio";
        };
    }

    public List<Timeline> listarLinhaDoTempo(Long userId) {

        List<Timeline> atividades = timelineRepository.findByUserIdOrderByTimeDesc(userId);

        if (atividades.isEmpty()) {
            return List.of();
        }

        return atividades;
    }
    
}
