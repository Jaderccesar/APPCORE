package com.example.appcore.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {

    PagamentoService pagamentoService = new PagamentoService();

    @Test
    void deveRetornarFalsoParaNumeroCartaoInvalido() {

        boolean resultado = pagamentoService.validaCartaoCreditoValido(
                "1111 0000 1111 0000",
                "Leandro da Silva",
                "04/30",
                "123"
        );

        assertFalse(resultado, "Cartão com número inválido deve retornar false");
    }

    // utilizando Algoritmo de Luhn
    @Test
    void deveAceitarCartaoValido() {
        boolean resultado = pagamentoService.validaCartaoCreditoValido(
                "4111 1111 1111 1111",
                "Leandro da Silva",
                "09/29",
                "123"
        );

        assertTrue(resultado, "Cartão válido deve retornar true");
    }

    @Test
    void deveFalharQuandoNomeVazio() {
        boolean resultado = pagamentoService.validaCartaoCreditoValido(
                "4111 1111 1111 1111",
                " ",
                "09/29",
                "123"
        );

        assertFalse(resultado);
    }

    @Test
    void deveFalharQuandoCvvInvalido() {
        boolean resultado = pagamentoService.validaCartaoCreditoValido(
                "4111 1111 1111 1111",
                "Leandro",
                "09/29",
                "12"
        );

        assertFalse(resultado);
    }

    @Test
    void deveFalharQuandoValorPixForInvalido() {

        boolean resultado = pagamentoService.pagamentoPix(
                -39.90,
                "leandro@gmail.com"
        );

        assertFalse(resultado, "Valor PIX negativo deve retornar false");
    }

    @Test
    void deveFalharQuandoChavePixVazia() {
        boolean resultado = pagamentoService.pagamentoPix(
                50.00,
                ""
        );

        assertFalse(resultado);
    }

    @Test
    void deveRealizarPixValido() {
        boolean resultado = pagamentoService.pagamentoPix(
                49.90,
                "leandro@gmail.com"
        );

        assertTrue(resultado, "Pagamento PIX válido deve retornar true");
    }

    @Test
    void deveGerarChavePixValida() {
        PagamentoService pagamentoService = new PagamentoService();

        String chave = pagamentoService.criarChavePix(
                100.00,
                "Leandro da Silva",
                1L
        );

        assertNotNull(chave, "Chave Pix não pode ser nula");
        assertTrue(chave.startsWith("PIX-1-"), "Chave Pix deve começar com prefixo esperado");
        assertTrue(chave.length() > 10, "Chave Pix deve ter tamanho adequado");
    }

    @Test
    void deveGerarChavesPixUnicasParaMesmaRequisicao() {
        PagamentoService pagamentoService = new PagamentoService();

        String chave1 = pagamentoService.criarChavePix(
                100.00, "Leandro da Silva", 10L
        );

        String chave2 = pagamentoService.criarChavePix(
                100.00, "Leandro da Silva", 10L
        );

        assertNotNull(chave1);
        assertNotNull(chave2);
        assertNotEquals(chave1, chave2, "Cada chamada deve gerar uma chave Pix única");
    }


}
