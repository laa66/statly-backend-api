package com.laa66.statlyapp.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MainstreamScoreDTO {
    private double score;

    private String range;

    private double difference;

    private LocalDate date;

    public MainstreamScoreDTO(double score, String range) {
        this.score = score;
        this.range = range;
    }

}
