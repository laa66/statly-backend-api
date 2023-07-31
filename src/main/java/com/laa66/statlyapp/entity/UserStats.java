package com.laa66.statlyapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_stats")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
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
    private String ig;
    private String fb;
    private String twitter;

    @Column(name = "battle_count")
    private int battleCount;

    public UserStats(long id, double energy, double tempo, double mainstream, double boringness, long points, int battleCount) {
        this.id = id;
        this.energy = energy;
        this.tempo = tempo;
        this.mainstream = mainstream;
        this.boringness = boringness;
        this.points = points;
        this.battleCount = battleCount;
    }


}
