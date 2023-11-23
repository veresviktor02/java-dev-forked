package core.service.impl;

import com.epam.training.ticketservice.core.dto.ScreeningDto;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;
import com.epam.training.ticketservice.core.model.Movie;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.model.Screening;
import com.epam.training.ticketservice.core.repository.MovieRepository;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.repository.ScreeningRepository;
import com.epam.training.ticketservice.core.service.impl.ScreeningServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ScreeningServiceImplTest {

    private final MovieRepository movieRepository = Mockito.mock(MovieRepository.class);
    private final RoomRepository roomRepository = Mockito.mock(RoomRepository.class);
    private final ScreeningRepository screeningRepository = Mockito.mock(ScreeningRepository.class);
    private final ScreeningServiceImpl underTest = new ScreeningServiceImpl(
            screeningRepository,
            movieRepository,
            roomRepository
    );
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Movie movie = new Movie("Terminator", "Action", 108);
    private final Room room = new Room("Apollo", 10, 10);
    private final Screening screening = new Screening(
            movie,
            room,
            LocalDateTime.parse(
                    "2023-11-20 10:00",
                    dateTimeFormatter
            )
    );

    @Test
    public void testDeleteScreeningShouldDeleteScreeningWhenScreeningExists() throws DoesNotExistException {
        //Given
        when(movieRepository.findByName(movie.getName())).thenReturn(Optional.of(movie));
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.of(room));
        when(screeningRepository.findScreeningByMovieAndRoomAndScreeningTime(
                screening.getMovie(),
                screening.getRoom(),
                screening.getScreeningTime())
        ).thenReturn(Optional.of(screening));
        doNothing().when(screeningRepository).delete(screening);

        //When
        underTest.deleteScreening(
                screening.getMovie().getName(),
                screening.getRoom().getName(),
                screening.getScreeningTime()
        );

        //Then
        verify(screeningRepository, never()).save(screening);
        verify(screeningRepository).delete(screening);
    }

    @Test
    public void testDeleteScreeningShouldNotDeleteScreeningWhenScreeningDoesNotExist() {
        //Given
        when(movieRepository.findByName(movie.getName())).thenReturn(Optional.of(movie));
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.of(room));
        when(screeningRepository.findScreeningByMovieAndRoomAndScreeningTime(
                screening.getMovie(),
                screening.getRoom(),
                screening.getScreeningTime())
        ).thenReturn(Optional.empty());

        //When
        assertThrows(DoesNotExistException.class,
                () -> underTest.deleteScreening(
                        screening.getMovie().getName(),
                        screening.getRoom().getName(),
                        screening.getScreeningTime())
        );

        //Then
        verify(screeningRepository, never()).save(screening);
        verify(screeningRepository, never()).delete(screening);
    }

    @Test
    public void testDeleteScreeningShouldReturnDoesNotExistExceptionWhenMovieAndRoomDoNotExist() {
        //Given
        when(screeningRepository.findScreeningByMovieAndRoomAndScreeningTime(
                screening.getMovie(),
                screening.getRoom(),
                screening.getScreeningTime()
        )).thenReturn(Optional.empty());

        //When
        assertThrows(DoesNotExistException.class,
                () -> underTest.deleteScreening(
                        screening.getMovie().getName(),
                        screening.getRoom().getName(),
                        screening.getScreeningTime())
        );

        //Then
        verify(screeningRepository, never()).save(screening);
        verify(screeningRepository, never()).delete(screening);
    }

    @Test
    public void testDeleteScreeningShouldReturnDoesNotExistExceptionWhenRoomDoesNotExist() {
        //Given
        when(movieRepository.findByName(movie.getName())).thenReturn(Optional.of(movie));
        when(screeningRepository.findScreeningByMovieAndRoomAndScreeningTime(
                screening.getMovie(),
                screening.getRoom(),
                screening.getScreeningTime()
        )).thenReturn(Optional.empty());

        //When
        assertThrows(DoesNotExistException.class,
                () -> underTest.deleteScreening(
                        screening.getMovie().getName(),
                        screening.getRoom().getName(),
                        screening.getScreeningTime())
        );

        //Then
        verify(screeningRepository, never()).save(screening);
        verify(screeningRepository, never()).delete(screening);
    }

    @Test
    public void testDeleteScreeningShouldReturnDoesNotExistExceptionWhenAMovieDoesNotExist() {
        //Given
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.of(room));
        when(screeningRepository.findScreeningByMovieAndRoomAndScreeningTime(screening.getMovie(), screening.getRoom(),
                screening.getScreeningTime())
        ).thenReturn(Optional.empty());

        //When
        assertThrows(DoesNotExistException.class,
                () -> underTest.deleteScreening(
                        screening.getMovie().getName(),
                screening.getRoom().getName(), screening.getScreeningTime())
        );

        //Then
        verify(screeningRepository, never()).save(screening);
        verify(screeningRepository, never()).delete(screening);
    }

    @Test
    public void testScreeningListShouldReturnScreeningListWhenThereAreScreenings() {
        //Given
        Mockito.when(screeningRepository.findAll()).thenReturn(Collections.singletonList(screening));

        //When
        List<ScreeningDto> screeningDtoList = underTest.screeningList();

        //Then
        Mockito.verify(screeningRepository).findAll();
        assertEquals(1, screeningDtoList.size());
        assertEquals(screening.getMovie().getName(), screeningDtoList.get(0).getMovieDto().getName());
        assertEquals(screening.getRoom().getName(), screeningDtoList.get(0).getRoomDto().getName());
        assertEquals(screening.getScreeningTime(), screeningDtoList.get(0).getScreeningTime());
    }

    @Test
    public void testRoomListShouldReturnEmptyListWhenRoomListIsEmpty() {
        // Given

        // When
        List<ScreeningDto> screeningDtoList = underTest.screeningList();

        // Then
        assertEquals(emptyList(), screeningDtoList);
    }
}
