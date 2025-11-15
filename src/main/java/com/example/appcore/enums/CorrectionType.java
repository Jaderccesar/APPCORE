package com.example.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CorrectionType {
    @JsonProperty("MANUAL")
    MANUAL,
    @JsonProperty("AUTOMATICO")
    AUTOMATIC
}
