package com.epam.training.ticketservice.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public enum Role {
        USER,
        ADMIN
    }
}
