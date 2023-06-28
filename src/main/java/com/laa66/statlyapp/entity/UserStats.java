package com.laa66.statlyapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_stats")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double energy;
    private double tempo;
    private double mainstream;
    private double boringness;
    private long points;
}
