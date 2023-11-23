package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.dto.ScreeningDto;
import com.epam.training.ticketservice.core.exceptions.BreakPeriodException;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;
import com.epam.training.ticketservice.core.exceptions.OverlappingException;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    @Override
    public void createScreening(String movie, String room, LocalDateTime screeningTime)
            throws DoesNotExistException, OverlappingException, BreakPeriodException {
        Optional<Movie> movieOptional = movieRepository.findByName(movie);
        Optional<Room> roomOptional = roomRepository.findByName(room);
        checkMovieAndRoomExistence(movieOptional, roomOptional);
        Screening returnScreening = new Screening(movieOptional.get(), roomOptional.get(), screeningTime);
        canCreateScreening(returnScreening);
        screeningRepository.save(returnScreening);
    }

    private void canCreateScreening(Screening returnScreening)
            throws OverlappingException, BreakPeriodException {
        Optional<Screening> screeningList = screeningRepository.findScreeningByRoom(returnScreening.getRoom());

        if (screeningList.isEmpty()) {
            return;
        }

        for (Screening iterator : screeningList.stream().toList()) {
            LocalDateTime screeningStart = returnScreening.getScreeningTime();
            LocalDateTime screeningEnd = screeningStart.plusMinutes(returnScreening.getMovie().getLength());
            LocalDateTime screeningBreakPeriod = screeningEnd.plusMinutes(10);

            LocalDateTime iteratorStart = iterator.getScreeningTime();
            LocalDateTime iteratorEnd = iteratorStart.plusMinutes(iterator.getMovie().getLength());
            LocalDateTime iteratorBreakPeriod = iteratorEnd.plusMinutes(10);

            boolean isScreeningBetweenIterators = isBetween(screeningStart, iteratorStart, iteratorEnd)
                    || isBetween(screeningEnd, iteratorStart, iteratorEnd)
                    || isInside(iteratorStart, iteratorEnd, screeningStart, screeningEnd);
            boolean isScreeningBeforeIteratorBreak = isBefore(screeningEnd, iteratorStart)
                    && isAfter(screeningBreakPeriod, iteratorStart);
            boolean isScreeningAfterIteratorEnd = isAfter(screeningStart, iteratorEnd)
                    && isBefore(screeningStart, iteratorBreakPeriod);

            if (isScreeningBetweenIterators || isEqual(screeningStart, iteratorStart)
                    || isEqual(screeningEnd, iteratorEnd)) {
                throw new OverlappingException("There is an overlapping screening");
            } else if (isScreeningBeforeIteratorBreak || isScreeningAfterIteratorEnd) {
                throw new BreakPeriodException(
                        "This would start in the break period after another screening in this room"
                );
            }
        }
    }

    private static boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return dateTime.isAfter(start) && dateTime.isBefore(end);
    }

    private static boolean isInside(LocalDateTime start, LocalDateTime end,
                                    LocalDateTime innerStart, LocalDateTime innerEnd) {
        return innerStart.isAfter(start) && innerEnd.isBefore(end);
    }

    private static boolean isEqual(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isEqual(dateTime2);
    }

    private static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isBefore(dateTime2);
    }

    private static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isAfter(dateTime2);
    }

    private void checkMovieAndRoomExistence(Optional<Movie> movie, Optional<Room> room) throws DoesNotExistException {
        if (movie.isEmpty() && room.isEmpty()) {
            throw new DoesNotExistException("The given movie and room do not exist");
        } else if (room.isEmpty()) {
            throw new DoesNotExistException("The given room does not exist");
        } else if (movie.isEmpty()) {
            throw new DoesNotExistException("The given movie does not exist");
        }
    }

    @Override
    public void deleteScreening(String movie, String room, LocalDateTime screeningTime) throws DoesNotExistException {
        Optional<Movie> movieOptional = movieRepository.findByName(movie);
        Optional<Room> roomOptional = roomRepository.findByName(room);
        checkMovieAndRoomExistence(movieOptional, roomOptional);
        if (screeningRepository.findScreeningByMovieAndRoomAndScreeningTime(
                movieOptional.get(),
                roomOptional.get(),
                screeningTime
        ).isPresent()) {
            Screening screening = screeningRepository.findScreeningByMovieAndRoomAndScreeningTime(
                    movieOptional.get(),
                    roomOptional.get(),
                    screeningTime
            ).get();
            screeningRepository.delete(screening);
        } else {
            throw new DoesNotExistException("The given screening does not exist.");
        }
    }

    @Override
    public List<ScreeningDto> screeningList() {
        return screeningRepository.findAll().stream().map(ScreeningDto::new).toList();
    }
}
