package com.example.appcore.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TimelineVisibility {

    @JsonProperty("PUBLICO")
    PUBLIC,
    @JsonProperty("PRIVADO")
    PRIVATE
}
