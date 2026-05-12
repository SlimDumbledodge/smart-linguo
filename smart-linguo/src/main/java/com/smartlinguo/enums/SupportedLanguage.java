package com.smartlinguo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum SupportedLanguage {
    EN("en", "English"),
    FR("fr", "French"),
    ES("es", "Spanish"),
    DE("de", "German"),
    IT("it", "Italian"),
    PT("pt", "Portuguese"),
    NL("nl", "Dutch"),
    RU("ru", "Russian"),
    UK("uk", "Ukrainian"),
    PL("pl", "Polish"),
    TR("tr", "Turkish"),
    AR("ar", "Arabic"),
    HE("he", "Hebrew"),
    HI("hi", "Hindi"),
    ZH("zh", "Chinese"),
    JA("ja", "Japanese"),
    KO("ko", "Korean"),
    TH("th", "Thai"),
    VI("vi", "Vietnamese"),
    ID("id", "Indonesian");

    private final String code;
    private final String label;

    SupportedLanguage(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static SupportedLanguage fromValue(String value) {
        if (value == null) {
            return null;
        }

        return Arrays.stream(values())
            .filter(lang ->
                lang.code.equalsIgnoreCase(value.trim()) ||
                lang.name().equalsIgnoreCase(value.trim()))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("Unsupported language: " + value));
    }
}