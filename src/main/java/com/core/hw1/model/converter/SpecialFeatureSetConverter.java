package com.core.hw1.model.converter;

import com.core.hw1.enumeration.SpecialFeature;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class SpecialFeatureSetConverter implements AttributeConverter<Set<SpecialFeature>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<SpecialFeature> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
            .map(SpecialFeature::getValue)
            .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public Set<SpecialFeature> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(SpecialFeature::fromValue)
                .collect(Collectors.toSet());
    }
}
