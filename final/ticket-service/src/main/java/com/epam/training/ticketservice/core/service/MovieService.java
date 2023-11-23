package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.dto.MovieDto;
import com.epam.training.ticketservice.core.exceptions.AlreadyExistsException;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;
import com.epam.training.ticketservice.core.model.Movie;

import java.util.List;

public interface MovieService {

    void createMovie(String name, String genre, int length)
            throws AlreadyExistsException;

    void updateMovie(String name, String genre, int length)
            throws DoesNotExistException;

    void deleteMovie(String name)
            throws DoesNotExistException;

    List<MovieDto> movieList();
}
