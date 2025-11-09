package com.example.appcore.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PostStatus {

    @JsonProperty("ATIVA")
    ACTIVE,
    @JsonProperty("OCULTA")
    HIDDEN,
    @JsonProperty("REMOVIDA")
    REMOVED
}
