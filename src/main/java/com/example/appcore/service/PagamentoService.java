package com.example.appcore.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PagamentoService {

    public boolean validaCartaoCreditoValido(String numero, String nome, String validade, String cvv) {

        if (numero == null) return false;

        String clean = numero.replace(" ", "");

        if (clean.length() != 16) return false;

        if (!clean.chars().allMatch(Character::isDigit)) return false;

        if (nome == null || nome.trim().isEmpty()) return false;

        if (validade == null || !validade.matches("\\d{2}/\\d{2}")) return false;

        if (cvv == null || !cvv.matches("\\d{3}")) return false;

        return luhnCheck(clean);
    }

    private boolean luhnCheck(String ccNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = ccNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));

            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }

            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    public boolean pagamentoPix(double valor, String chavePix) {
        return valor > 0 && chavePix != null && !chavePix.trim().isEmpty();
    }

    public String criarChavePix(double valor, String nomeComprador, Long cursoId) {

        if (valor <= 0) throw new IllegalArgumentException("Valor inválido");
        if (nomeComprador == null || nomeComprador.trim().isEmpty()) throw new IllegalArgumentException("Nome inválido");
        if (cursoId == null) throw new IllegalArgumentException("Curso inválido");

        String chave = "PIX-" + cursoId + "-" + UUID.randomUUID().toString();

        System.out.println("Chave PIX gerada: " + chave);

        return chave;
    }


}

