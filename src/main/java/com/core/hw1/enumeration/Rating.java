package com.core.hw1.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Rating {

    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String value;

    Rating(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Rating fromValue(String value) {
        for (Rating rating : values()) {
            if (rating.value.equalsIgnoreCase(value)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Unknown rating: " + value);
    }

}
