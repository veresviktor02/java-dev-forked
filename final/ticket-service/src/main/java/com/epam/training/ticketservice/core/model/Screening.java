package com.epam.training.ticketservice.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Screening {

    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "MOVIE_ID", referencedColumnName = "ID")
    private Movie movie;
    @ManyToOne
    @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")
    private Room room;
    private LocalDateTime screeningTime;

    public Screening(Movie movie, Room room, LocalDateTime screeningTime) {
        this.movie = movie;
        this.room = room;
        this.screeningTime = screeningTime;
    }
}
