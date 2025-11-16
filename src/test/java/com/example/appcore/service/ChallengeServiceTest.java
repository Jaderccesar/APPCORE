package com.example.appcore.service;

import com.example.appcore.enums.Difficulty;
import com.example.appcore.model.Challenge;
import com.example.appcore.repository.ChallengeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    @Test
    void deveImpedirCriacaoDesafioComNomeInvalido() {

        Challenge challenge = new Challenge();
        challenge.setTitle(" ");
        challenge.setDescription("Resolver problema lógico usando código");
        challenge.setDifficulty(Difficulty.valueOf("INICIANTE"));

        assertThrows(IllegalArgumentException.class,
                () -> challengeService.save(challenge),
                "Nome inválido deve impedir criação do desafio");

        verify(challengeRepository, never()).save(any());
    }

    @Test
    void deveImpedirCriacaoDesafioComDescricaoInvalida() {

        Challenge challenge = new Challenge();
        challenge.setTitle("Desafio de Programação");
        challenge.setDescription(" ");
        challenge.setDifficulty(Difficulty.valueOf("INICIANTE"));

        assertThrows(IllegalArgumentException.class,
                () -> challengeService.save(challenge),
                "Descrição inválida deve impedir criação do desafio");

        verify(challengeRepository, never()).save(any());
    }

    @Test
    void deveCriarDesafioValido() {

        Challenge challenge = new Challenge();
        challenge.setTitle("Desafio de Lógica");
        challenge.setDescription("Resolva problemas de lógica");
        challenge.setDifficulty(Difficulty.valueOf("INICIANTE"));

        when(challengeRepository.save(any(Challenge.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Challenge salvo = challengeService.save(challenge);

        assertNotNull(salvo);
        assertEquals("Desafio de Lógica", salvo.getTitle());
        verify(challengeRepository, times(1)).save(challenge);
    }
}
