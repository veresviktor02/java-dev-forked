package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.dto.ScreeningDto;
import com.epam.training.ticketservice.core.exceptions.BreakPeriodException;
import com.epam.training.ticketservice.core.exceptions.DoesNotExistException;
import com.epam.training.ticketservice.core.exceptions.OverlappingException;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningService {

    void createScreening(String movie, String room, LocalDateTime screeningTime)
            throws DoesNotExistException, OverlappingException, BreakPeriodException;

    void deleteScreening(String movie, String room, LocalDateTime screeningTime)
            throws DoesNotExistException;

    List<ScreeningDto> screeningList();
}
