package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TranslationEntry {

    @JsonProperty("code")
    private String mnemonic;

    // @JsonProperty("message")
    private String message;
}
