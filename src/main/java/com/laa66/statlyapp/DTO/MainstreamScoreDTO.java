package com.laa66.statlyapp.DTO;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class MainstreamScoreDTO {
    private double score;

    private String range;

    private double difference;

    private LocalDate date;

    public void withRange(String range) {
        this.range = range;
    }

    public void withDifference(double difference) {
        this.difference = difference;
    }

    public void withDate(LocalDate date) {
        this.date = date;
    }

}
