package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.laa66.statlyapp.model.PlaybackEvent;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecentlyPlayedDTO {

    String total;

    @JsonProperty("items")
    List<PlaybackEvent> playbackEvents;
}
