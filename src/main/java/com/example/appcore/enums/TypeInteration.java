package com.example.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Optional;

public enum TypeInteration {

    @JsonProperty("COMENTARIO")
    COMMENT,
    @JsonProperty("CURTIDA")
    LIKE,
    @JsonProperty("SEGUIDO")
    FOLLOWED,
    @JsonProperty("POSTAGEM")
    POST,

    @JsonProperty("DESAFIO CONCLUIDO")
    CHALLENGE_COMPLETED;

    public static Optional<TypeInteration> fromString(String value) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
