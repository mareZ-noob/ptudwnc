package com.core.hw1.model.converter;

import com.core.hw1.enumeration.Rating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {

    @Override
    public String convertToDatabaseColumn(Rating rating) {
        if (rating == null) {
            return null;
        }
        return rating.getValue();
    }

    @Override
    public Rating convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return Rating.fromValue(value);
    }
}
