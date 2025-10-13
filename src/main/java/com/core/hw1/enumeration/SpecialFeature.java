package com.core.hw1.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SpecialFeature {
    
    TRAILERS("Trailers"),
    COMMENTARIES("Commentaries"),
    DELETED_SCENES("Deleted Scenes"),
    BEHIND_THE_SCENES("Behind the Scenes");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    SpecialFeature(String value) {
        this.value = value;
    }

    public static SpecialFeature fromValue(String value) {
        for (SpecialFeature feature : values()) {
            if (feature.value.equalsIgnoreCase(value)) {
                return feature;
            }
        }
        throw new IllegalArgumentException("Unknown special feature: " + value);
    }

}
