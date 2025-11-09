package com.example.appcore.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Gender {
    @JsonProperty("MASCULINO")
    MALE,
    @JsonProperty("FEMININO")
    FEMALE,
    @JsonProperty("OUTROS")
    OTHER
}
