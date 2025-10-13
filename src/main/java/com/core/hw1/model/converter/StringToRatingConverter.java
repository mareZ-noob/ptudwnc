package com.core.hw1.model.converter;

import com.core.hw1.enumeration.Rating;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRatingConverter implements Converter<String, Rating> {

    @Override
    public Rating convert(String source) {
        try {
            String enumName = source.toUpperCase().replace('-', '_');
            return Rating.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown rating: " + source, e);
        }
    }

}
