package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.dto.MovieDto;
import com.epam.training.ticketservice.core.exceptions.AlreadyExistsException;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public void createMovie(String name, String genre, int length)
            throws AlreadyExistsException {
        if (movieRepository.findByName(name).isPresent()) {
            throw new AlreadyExistsException("The movie already exists.");
        } else {
            Movie movie = new Movie(name, genre, length);
            movieRepository.save(movie);
        }

    }

    @Override
    public void updateMovie(String name, String genre, int length)
            throws DoesNotExistException {
        if (movieRepository.findByName(name).isPresent()) {
            Movie movie = movieRepository.findByName(name).get();
            movie.setGenre(genre);
            movie.setLength(length);
            movieRepository.save(movie);
        } else {
            throw new DoesNotExistException("The movie does not exist.");
        }
    }

    @Override
    public void deleteMovie(String name)
            throws DoesNotExistException {
        if (movieRepository.findByName(name).isPresent()) {
            Movie movie = movieRepository.findByName(name).get();
            movieRepository.delete(movie);
        } else {
            throw new DoesNotExistException("The movie does not exist.");
        }
    }

    @Override
    public List<MovieDto> movieList() {
        return movieRepository.findAll().stream().map(MovieDto::new).toList();
    }
}
