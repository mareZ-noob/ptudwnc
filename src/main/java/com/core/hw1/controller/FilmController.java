package com.core.hw1.controller;

import com.core.hw1.enumeration.Rating;
import com.core.hw1.exception.ResourceNotFoundException;
import com.core.hw1.model.Film;
import com.core.hw1.model.Language;
import com.core.hw1.repository.FilmRepository;
import com.core.hw1.repository.LanguageRepository;
import com.core.hw1.viewmodel.ErrorVm;
import com.core.hw1.viewmodel.FilmRequestVm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/films")
@Tag(
        name = "Film Controller",
        description = "REST APIs for Film"
)
public class FilmController {

    private final FilmRepository filmRepository;
    private final LanguageRepository languageRepository;


    public FilmController(FilmRepository filmRepository, LanguageRepository languageRepository) {
        this.filmRepository = filmRepository;
        this.languageRepository = languageRepository;
    }

    @Operation(summary = "Get all films with pagination", description = "Returns a paginated list of all films.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = List.class))),
    })
    @GetMapping
    public List<FilmRequestVm> getAllFilms(
            @Parameter(description = "Page number, starting from 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return filmRepository.findAll(pageable).stream().map(this::mapToFilmRequestVm).toList();
    }

    @Operation(summary = "Get a film by ID", description = "Returns a single film by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved film",
                    content = @Content(schema = @Schema(implementation = FilmRequestVm.class))),
            @ApiResponse(responseCode = "404", description = "Film not found",
                    content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<FilmRequestVm> getFilmById(@Parameter(description = "ID of the film to be retrieved") @PathVariable Short id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found with id: " + id));
        return ResponseEntity.ok(mapToFilmRequestVm(film));
    }

    @Operation(summary = "Create a new film", description = "Adds a new film to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Film created successfully",
                    content = @Content(schema = @Schema(implementation = FilmRequestVm.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    @PostMapping
    public ResponseEntity<FilmRequestVm> createFilm(@Valid @RequestBody FilmRequestVm filmRequestVm) {
        Film film = mapToFilm(filmRequestVm, new Film());
        Film savedFilm = filmRepository.save(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToFilmRequestVm(savedFilm));
    }

    @Operation(summary = "Update an existing film", description = "Updates the details of an existing film by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film updated successfully",
                    content = @Content(schema = @Schema(implementation = FilmRequestVm.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "404", description = "Film not found",
                    content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<FilmRequestVm> updateFilm(@Parameter(description = "ID of the film to update") @PathVariable Short id,
                                                    @Valid @RequestBody FilmRequestVm filmRequestVm) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found with id: " + id));

        Film updatedFilm = mapToFilm(filmRequestVm, existingFilm);
        filmRepository.save(updatedFilm);
        return ResponseEntity.ok(mapToFilmRequestVm(updatedFilm));
    }

    @Operation(summary = "Delete a film", description = "Deletes a film from the database by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Film deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Film not found",
                    content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@Parameter(description = "ID of the film to delete") @PathVariable Short id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found with id: " + id));
        filmRepository.delete(film);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find films by release year", description = "Gets a list of all films released in a specific year.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = List.class))),
    })
    @GetMapping("/search/year/{year}")
    public List<FilmRequestVm> findFilmsByYear(@Parameter(description = "The 4-digit release year", example = "2006") @PathVariable Integer year) {
        List<Film> films = filmRepository.findByReleaseYear(year);

        return films.stream().map(this::mapToFilmRequestVm).toList();
    }

    @Operation(summary = "Find films by rating", description = "Gets a list of all films with a specific MPAA rating.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = List.class))),
    })
    @GetMapping("/search/rating/{rating}")
    public List<FilmRequestVm> findFilmsByRating(@Parameter(description = "The rating to filter by (e.g., G, PG, PG-13, R, NC-17)", example = "PG-13") @PathVariable Rating rating) {
        List<Film> films = filmRepository.findByRating(rating);

        return films.stream().map(this::mapToFilmRequestVm).toList();
    }

    @Operation(summary = "Find long films", description = "Gets a list of films longer than a given duration in minutes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = List.class))),
    })
    @GetMapping("/search/longer-than/{minutes}")
    public List<FilmRequestVm> findFilmsLongerThan(@Parameter(description = "The minimum length of the film in minutes", example = "180") @PathVariable Integer minutes) {
        List<Film> films = filmRepository.findByLengthGreaterThan(minutes);

        return films.stream().map(this::mapToFilmRequestVm).toList();
    }

    @Operation(summary = "Search films by title", description = "Finds films whose title contains the given keyword (case-insensitive).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = List.class))),
    })
    @GetMapping("/search/title")
    public List<FilmRequestVm> searchFilmsByTitle(
            @Parameter(description = "Keyword to search for in the film title", example = "matrix") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Film> films = filmRepository.findByTitleContainingIgnoreCase(keyword, PageRequest.of(page, size));
        return films.stream().map(this::mapToFilmRequestVm).toList();
    }

    @Operation(summary = "Find films by language", description = "Gets a list of all films available in a specific language.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = List.class))),
    })
    @GetMapping("/search/language/{languageName}")
    public List<FilmRequestVm> findFilmsByLanguage(@Parameter(description = "The name of the language", example = "English") @PathVariable String languageName) {
        List<Film> films = filmRepository.findByLanguageName(languageName);

        return films.stream().map(this::mapToFilmRequestVm).toList();
    }

    private Film mapToFilm(FilmRequestVm filmRequestVm, Film film) {
        log.info("Map FilmRequestVm {} to Film entity", filmRequestVm);
        film.setTitle(filmRequestVm.getTitle());
        film.setDescription(filmRequestVm.getDescription());
        film.setReleaseYear(filmRequestVm.getReleaseYear());
        film.setRentalDuration(filmRequestVm.getRentalDuration());
        film.setRentalRate(filmRequestVm.getRentalRate());
        film.setLength(filmRequestVm.getLength());
        film.setReplacementCost(filmRequestVm.getReplacementCost());
        film.setRating(filmRequestVm.getRating());
        film.setSpecialFeatures(filmRequestVm.getSpecialFeatures());
        film.setLastUpdate(Instant.now());

        Language lang = languageRepository.findById(filmRequestVm.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language not found with id: " + filmRequestVm.getLanguageId()));
        film.setLanguage(lang);

        if (filmRequestVm.getOriginalLanguageId() != null) {
            Language origLang = languageRepository.findById(filmRequestVm.getOriginalLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Original language not found with id: " + filmRequestVm.getOriginalLanguageId()));
            film.setOriginalLanguage(origLang);
            log.info("Set originalLanguage to language with ID: {}", filmRequestVm.getOriginalLanguageId());
        } else {
            log.info("Original language ID is null, setting originalLanguage to null");
            film.setOriginalLanguage(null);
        }

        return film;
    }

    private FilmRequestVm mapToFilmRequestVm(Film film) {
        log.info("Map Film entity {} to FilmRequestVm", film);
        return FilmRequestVm.builder()
                .title(film.getTitle())
                .description(film.getDescription())
                .length(film.getLength())
                .rentalRate(film.getRentalRate())
                .rentalDuration(film.getRentalDuration())
                .languageId(film.getLanguage().getId())
                .originalLanguageId(film.getOriginalLanguage() != null ? film.getOriginalLanguage().getId() : null)
                .rating(film.getRating())
                .releaseYear(film.getReleaseYear())
                .replacementCost(film.getReplacementCost())
                .specialFeatures(film.getSpecialFeatures())
                .build();
    }

}
