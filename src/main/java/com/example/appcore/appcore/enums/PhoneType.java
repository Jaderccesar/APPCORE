package com.example.appcore.appcore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PhoneType {
    @JsonProperty("RESIDENCIAL")
    RESIDENTIAL,
    @JsonProperty("COMERCIAL")
    COMMERCIAL,
    @JsonProperty("CELULAR")
    MOBILE,
    @JsonProperty("WHATSAPP")
    WHATSAPP,
    @JsonProperty("OTHER")
    OTHER
}
