package com.example.appcore.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CreateStatus {
    @JsonProperty("RASCUNHO")
    DRAFT,

    @JsonProperty("PUBLICADO")
    PUBLISHED,

    @JsonProperty("ARQUIVADO")
    ARCHIVED
}

