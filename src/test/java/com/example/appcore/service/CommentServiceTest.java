package com.example.appcore.service;

import com.example.appcore.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

//Jader

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    // CT01 – Comentário válido
    @Test
    void deveValidarComentarioValido() {
        String comentario = "Ótimo curso!";

        boolean resultado = commentService.validarComentario(comentario);

        assertTrue(resultado);
    }

    // CT02 – Comentário vazio
    @Test
    void naoDeveValidarComentarioVazio() {
        String comentario = "";

        boolean resultado = commentService.validarComentario(comentario);

        assertFalse(resultado);
    }

    // CT03 – Comentário muito longo
    @Test
    void naoDeveValidarComentarioMuitoLongo() {
        String comentario = "a".repeat(501);

        boolean resultado = commentService.validarComentario(comentario);

        assertFalse(resultado);
    }

    // CT04 – Comentário com caracteres especiais perigosos
    @Test
    void naoDeveValidarComentarioComScript() {
        String comentario = "<script>alert(1)</script>";

        boolean resultado = commentService.validarComentario(comentario);

        assertFalse(resultado);
    }
}
