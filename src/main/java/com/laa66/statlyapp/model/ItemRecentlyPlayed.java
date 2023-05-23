package com.laa66.statlyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@JsonPropertyOrder({"track", "played_at"})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ItemRecentlyPlayed {

    private Track track;

    @JsonProperty("played_at")
    private ZonedDateTime playedAt;
}
