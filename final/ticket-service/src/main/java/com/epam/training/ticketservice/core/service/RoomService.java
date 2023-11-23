package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.dto.RoomDto;
import com.epam.training.ticketservice.core.exceptions.AlreadyExistsException;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;

import java.util.List;

public interface RoomService {

    void createRoom(String name, int row, int col)
            throws AlreadyExistsException;

    void updateRoom(String name, int row, int col)
            throws DoesNotExistException;

    void deleteRoom(String name)
            throws DoesNotExistException;

    List<RoomDto> roomList();
}
