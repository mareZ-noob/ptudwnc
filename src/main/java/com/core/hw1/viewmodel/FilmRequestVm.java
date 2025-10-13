package com.core.hw1.viewmodel;

import com.core.hw1.enumeration.Rating;
import com.core.hw1.enumeration.SpecialFeature;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Schema(description = "Data Transfer Object for creating or updating a film.")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmRequestVm {

    @NotBlank(message = "Title is mandatory and cannot be blank.")
    @Size(max = 255, message = "Title cannot be longer than 255 characters.")
    @Schema(description = "Title of the film.", example = "THE MATRIX", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "A brief summary of the film's plot.", example = "A computer hacker learns from mysterious rebels about the true nature of his reality...")
    private String description;

    @Min(value = 1888, message = "Release year must be 1888 or later.")
    @Schema(description = "The year the film was released.", example = "1999")
    private Integer releaseYear;

    @NotNull(message = "Language ID is mandatory.")
    @Schema(description = "Identifier for the film's language.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Byte languageId;

    @Schema(description = "Identifier for the film's original language, if different.", example = "1")
    private Byte originalLanguageId;

    @NotNull(message = "Rental duration is mandatory.")
    @Min(value = 1, message = "Rental duration must be at least 1 day.")
    @Schema(description = "The standard rental duration in days.", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Byte rentalDuration;

    @NotNull(message = "Rental rate is mandatory.")
    @DecimalMin(value = "0.0", message = "Rental rate must be a non-negative value.")
    @Digits(integer = 2, fraction = 2, message = "Rental rate format must be up to 2 digits before and 2 after the decimal point.")
    @Schema(description = "The cost to rent the film.", example = "4.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal rentalRate;

    @Min(value = 1, message = "Length must be at least 1 minute.")
    @Schema(description = "The duration of the film in minutes.", example = "136")
    private Integer length;

    @NotNull(message = "Replacement cost is mandatory.")
    @DecimalMin(value = "0.0", message = "Replacement cost must be non-negative.")
    @Digits(integer = 3, fraction = 2, message = "Replacement cost format must be up to 3 digits before and 2 after the decimal point.")
    @Schema(description = "The cost to replace the film if lost or damaged.", example = "19.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal replacementCost;

    @Schema(description = "The MPAA rating of the film.", example = "G")
    private Rating rating;

    @Schema(description = "A list of special features included with the film.", example = "[\"Trailers\", \"Deleted Scenes\"]")
    private Set<SpecialFeature> specialFeatures;

}
