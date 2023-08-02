package com.laa66.statlyapp.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;


@Entity
@Table(name = "user_tracks")
@AllArgsConstructor
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
}
