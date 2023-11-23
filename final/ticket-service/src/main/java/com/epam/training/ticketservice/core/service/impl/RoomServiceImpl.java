package com.epam.training.ticketservice.core.service.impl;

import com.epam.training.ticketservice.core.dto.RoomDto;
import com.epam.training.ticketservice.core.exceptions.AlreadyExistsException;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;
import com.epam.training.ticketservice.core.model.Room;
import com.epam.training.ticketservice.core.repository.RoomRepository;
import com.epam.training.ticketservice.core.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public void createRoom(String name, int row, int col) throws AlreadyExistsException {
        if (roomRepository.findByName(name).isPresent()) {
            throw new AlreadyExistsException("The room already exists.");
        } else {
            Room room = new Room(name, row, col);
            roomRepository.save(room);
        }
    }

    @Override
    public void updateRoom(String name, int row, int col) throws DoesNotExistException {
        if (roomRepository.findByName(name).isPresent()) {
            Room room = roomRepository.findByName(name).get();
            room.setRows(row);
            room.setCols(col);
            roomRepository.save(room);
        } else {
            throw new DoesNotExistException("The room does not exist.");
        }
    }

    @Override
    public void deleteRoom(String name) throws DoesNotExistException {
        if (roomRepository.findByName(name).isPresent()) {
            Room room = roomRepository.findByName(name).get();
            roomRepository.delete(room);
        } else {
            throw new DoesNotExistException("The room does not exist.");
        }
    }

    @Override
    public List<RoomDto> roomList() {
        return roomRepository.findAll().stream().map(RoomDto::new).toList();
    }
}
