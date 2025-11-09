package com.example.appcore.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CourseStatus {
    @JsonProperty("RASCUNHO")
    DRAFT,

    @JsonProperty("PUBLICADO")
    PUBLISHED,

    @JsonProperty("ARQUIVADO")
    ARCHIVED
}

