package core.service.impl;

import com.epam.training.ticketservice.core.dto.RoomDto;
import com.epam.training.ticketservice.core.exceptions.AlreadyExistsException;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.service.RoomService;
import com.epam.training.ticketservice.core.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class RoomServiceImplTest {

    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final RoomService underTest = new RoomServiceImpl(roomRepository);
    private final String testName = "Tom Hardy Room";
    private final Room room = new Room(testName, 40, 30);
    private final Room updatedRoom = new Room(testName, 400, 300);

    @Test
    public void testCreateRoomShouldSaveRoomWhenRoomDoesNotExist() throws AlreadyExistsException {
        //Given
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.empty());

        //When
        underTest.createRoom(room.getName(), room.getRows(), room.getCols());

        //Then
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    public void testCreateRoomShouldNotSaveRoomWhenRoomDoesExist() {
        //Given
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.of(room));

        //When
        assertThrows(AlreadyExistsException.class,
                () -> underTest.createRoom(
                        room.getName(),
                        room.getRows(),
                        room.getCols()
                )
        );

        //Then
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    public void testUpdateRoomShouldUpdateRoomWhenRoomDoesExist() throws DoesNotExistException {
        //Given
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        //When
        underTest.updateRoom(room.getName(), updatedRoom.getRows(), updatedRoom.getCols());

        //Then
        verify(roomRepository).save(room);
    }

    @Test
    public void testUpdateRoomShouldNotUpdateRoomWhenRoomDoesNotExist() {
        //Given
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.empty());

        //When
        assertThrows(DoesNotExistException.class,
                () -> underTest.updateRoom(
                        room.getName(),
                        updatedRoom.getRows(),
                        updatedRoom.getCols()
                )
        );

        //Then
        verify(roomRepository, never()).save(room);
    }

    @Test
    public void testDeleteRoomShouldDeleteRoomWhenRoomDoesExist() throws DoesNotExistException {
        //Given
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.of(room));
        doNothing().when(roomRepository).delete(room);

        //When
        underTest.deleteRoom(room.getName());

        //Then
        verify(roomRepository, never()).save(room);
        verify(roomRepository).delete(room);
    }

    @Test
    public void testDeleteRoomShouldNotDeleteRoomWhenRoomDoesNotExist() {
        //Given
        when(roomRepository.findByName(room.getName())).thenReturn(Optional.empty());

        //When
        assertThrows(DoesNotExistException.class,
                () -> underTest.deleteRoom(
                        room.getName()
                )
        );

        //Then
        verify(roomRepository, never()).save(room);
        verify(roomRepository, never()).delete(room);
    }

    @Test
    public void testRoomListShouldReturnRoomListWhenThereAreRooms() {
        //Given
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));

        //When
        List<RoomDto> roomDtoList = underTest.roomList();

        //Then
        verify(roomRepository).findAll();
        assertEquals(1, roomDtoList.size());
        assertEquals(room.getName(), roomDtoList.get(0).getName());
        assertEquals(room.getRows(), roomDtoList.get(0).getRows());
        assertEquals(room.getCols(), roomDtoList.get(0).getCols());
    }

    @Test
    public void testRoomListShouldReturnNothingWhenThereAreNoRooms() {
        //Given

        //When
        List<RoomDto> roomDtoList = underTest.roomList();

        //Then
        assertEquals(emptyList(), roomDtoList);
    }
}
