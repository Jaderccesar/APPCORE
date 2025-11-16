package com.example.appcore.service;

import com.example.appcore.service.MissionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

//Jader

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @InjectMocks
    private MissionService missionService;

    // CT01 – Pontos válidos
    @Test
    void deveValidarPontosDaMissaoQuandoValidos() {
        int pontos = 50;

        boolean resultado = missionService.validarPontosMissao(pontos);

        assertTrue(resultado, "Pontos válidos deveriam retornar TRUE");
    }

    // CT02 – Pontos inválidos (negativos)
    @Test
    void naoDeveValidarPontosDaMissaoQuandoNegativos() {
        int pontos = -10;

        boolean resultado = missionService.validarPontosMissao(pontos);

        assertFalse(resultado, "Pontos negativos deveriam retornar FALSE");
    }
}

