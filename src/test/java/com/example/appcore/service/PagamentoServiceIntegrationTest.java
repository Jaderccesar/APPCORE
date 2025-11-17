package com.example.appcore.service;

import com.example.appcore.model.Payment;
import com.example.appcore.enums.PaymentType;
import com.example.appcore.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PagamentoServiceIntegrationTest {

    @Autowired
    PagamentoService pagamentoService;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void devePersistirTransacaoPixValida() {

        boolean ok = pagamentoService.pagamentoPix(49.90, "teste@gmail.com");

        assertTrue(ok);

        List<Payment> pagamentos = paymentRepository.findAll();

        assertEquals(1, pagamentos.size());
        assertEquals(PaymentType.PIX, pagamentos.get(0).getType());
        assertEquals(49.90, pagamentos.get(0).getValue());
    }

    @Test
    void deveGerarEPersistirChavePix() {

        String chave = pagamentoService.criarChavePix(
                100.00,
                "Leandro da Silva",
                1L
        );

        assertNotNull(chave);
        assertTrue(chave.startsWith("PIX-1-"));

        Payment saved = paymentRepository.findAll().get(0);

        assertEquals("Leandro da Silva", saved.getPayerName());
        assertEquals(chave, saved.getPaymentKey());
    }

    @Test
    void deveGerarChavesDistintas() {

        String chave1 = pagamentoService.criarChavePix(10, "Leandro", 5L);
        String chave2 = pagamentoService.criarChavePix(10, "Leandro", 5L);

        assertNotEquals(chave1, chave2);
    }

    @Test
    void deveValidarCartaoValido() {

        boolean valido = pagamentoService.validaCartaoCreditoValido(
                "4111 1111 1111 1111",
                "Leandro da Silva",
                "08/29",
                "123"
        );

        assertTrue(valido);
    }

    @Test
    void deveRejeitarCartaoInvalido() {

        boolean valido = pagamentoService.validaCartaoCreditoValido(
                "1111 0000 1111 0000",
                "Leandro",
                "09/29",
                "123"
        );

        assertFalse(valido);
    }
}
