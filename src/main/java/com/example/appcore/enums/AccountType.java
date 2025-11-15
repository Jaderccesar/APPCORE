package com.example.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccountType {
    @JsonProperty("ESTUDANTE")
    STUDENT,
    @JsonProperty("PROFESSOR")
    TEACHER,
    @JsonProperty("EMPRESA")
    ENTERPRISE
}
