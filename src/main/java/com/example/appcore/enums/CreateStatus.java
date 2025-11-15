package com.example.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CreateStatus {
    @JsonProperty("RASCUNHO")
    DRAFT,

    @JsonProperty("PUBLICADO")
    PUBLISHED,

    @JsonProperty("ARQUIVADO")
    ARCHIVED
}

