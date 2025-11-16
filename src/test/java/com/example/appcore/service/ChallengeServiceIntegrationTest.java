package com.example.appcore.service;

import com.example.appcore.enums.Difficulty;
import com.example.appcore.model.Challenge;
import com.example.appcore.model.Course;
import com.example.appcore.repository.ChallengeRepository;
import com.example.appcore.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Jader

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ChallengeServiceIntegrationTest {

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    // CT01 – cadastrar desafio com curso existente
    @Test
    void deveCadastrarDesafioVinculadoAoCurso() {

        Course curso = new Course();
        curso.setTitle("Programação");
        curso.setDescription("Curso base");
        courseRepository.save(curso);

        Long courseId = curso.getId();

        Challenge desafio = new Challenge();
        desafio.setTitle("Desafio de Algoritmos");
        desafio.setDescription("Ordenar números");
        desafio.setDifficulty(Difficulty.INTERMEDIARIO);

        Challenge saved = challengeService.cadastrarDesafio(courseId, desafio);

        assertNotNull(saved.getId());
        assertEquals(courseId, saved.getCourse().getId());

        List<Challenge> desafiosDoCurso =
                challengeRepository.findAll().stream()
                        .filter(c -> c.getCourse().getId().equals(courseId))
                        .toList();

        assertEquals(1, desafiosDoCurso.size());
    }

    // CT02 – tentar cadastrar desafio com curso inexistente
    @Test
    void naoDeveCadastrarDesafioQuandoCursoNaoExiste() {

        Long courseIdInexistente = 999L;

        Challenge desafio = new Challenge();
        desafio.setTitle("Desafio de Estruturas");
        desafio.setDescription("Implementar pilha");
        desafio.setDifficulty(Difficulty.INTERMEDIARIO);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> challengeService.cadastrarDesafio(courseIdInexistente, desafio)
        );

        assertEquals("Curso não encontrado", ex.getMessage());
        assertEquals(0, challengeRepository.count());
    }
}

