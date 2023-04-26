package com.laa66.statlyapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "user_mainstream")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserMainstream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "time_range")
    private String range;

    private LocalDate date;

    private double score;

    public UserMainstream(long id, long userId, String range, LocalDate date, double score) {
        this.id = id;
        this.userId = userId;
        this.range = range;
        this.date = date;
        this.score = score;
    }
}
