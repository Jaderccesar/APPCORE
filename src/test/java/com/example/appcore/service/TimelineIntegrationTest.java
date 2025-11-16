package com.example.appcore.service;

import com.example.appcore.enums.TimelineVisibility;
import com.example.appcore.enums.TypeInteration;
import com.example.appcore.model.Student;
import com.example.appcore.model.Timeline;
import com.example.appcore.repository.StudentRepository;
import com.example.appcore.repository.TimelineRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
//Jader
public class TimelineIntegrationTest {

    @Autowired
    private TimelineService timelineService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TimelineRepository timelineRepository;

    @BeforeEach
    void cleanup() {
        timelineRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    void deveRegistrarEExibirInteracao() {
        // Arrange
        Student user = new Student();
        user.setName("Jader");
        user.setEmail("jader@example.com");
        user = studentRepository.save(user);

        // Act
        Timeline saved = timelineService.registrarInteracao(
                user.getId(),
                TypeInteration.CHALLENGE_COMPLETED,
                user
        );

        List<Timeline> timeline = timelineService.listarLinhaDoTempo(user.getId());

        // Assert
        assertFalse(timeline.isEmpty());
        assertEquals("Usuário concluiu um desafio", timeline.get(0).getDescription());
        assertEquals(saved.getId(), timeline.get(0).getId());
    }

    @Test
    void deveRetornarLinhaDoTempoVaziaQuandoNaoExistirInteracoes() {
        // Arrange
        Student user = new Student();
        user.setName("Maria");
        user.setEmail("maria@example.com");
        user = studentRepository.save(user);

        // Act
        List<Timeline> timeline = timelineService.listarLinhaDoTempo(user.getId());

        // Assert
        assertTrue(timeline.isEmpty());
    }
}
