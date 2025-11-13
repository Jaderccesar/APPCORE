package com.example.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("ATIVO")
    ACTIVE,
    @JsonProperty("INATIVO")
    INACTIVE
}
