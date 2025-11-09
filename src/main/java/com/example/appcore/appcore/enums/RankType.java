package com.example.appcore.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RankType {
    @JsonProperty("GERAL")
    GENERAL,
    @JsonProperty("CURSO")
    COURSE,
    @JsonProperty("EVENTO")
    EVENT
}
