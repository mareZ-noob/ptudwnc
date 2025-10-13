package com.core.hw1.repository;

import com.core.hw1.enumeration.Rating;
import com.core.hw1.enumeration.SpecialFeature;
import com.core.hw1.model.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Short> {
    List<Film> findByRating(Rating rating);

    List<Film> findByReleaseYear(Integer year);

    List<Film> findByLengthGreaterThan(Integer lengthIsGreaterThan);

    Page<Film> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT f FROM Film f JOIN f.language l WHERE l.name = :languageName")
    List<Film> findByLanguageName(String languageName);
}