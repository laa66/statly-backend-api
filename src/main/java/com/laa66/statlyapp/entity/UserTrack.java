package com.laa66.statlyapp.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;


@Entity
@Table(name = "user_tracks")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserTrack implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "time_range")
    private String range;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSON")
    private Map<String, Integer> tracks;

    private LocalDate date;

    public UserTrack(long id, long userId, String range, Map<String, Integer> tracks, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.range = range;
        this.tracks = tracks;
        this.date = date;
    }
}
