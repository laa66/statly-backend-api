package com.laa66.statlyapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laa66.statlyapp.model.mapbox.Coordinates;
import com.laa66.statlyapp.model.mapbox.Location;
import com.laa66.statlyapp.model.spotify.Image;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
@Builder
public class UserDTO {

    private final String id;

    private final String uri;

    private final String email;

    @JsonProperty("display_name")
    private final String name;

    private final List<Image> images;

    private final long points;

    private final Coordinates coordinates;

    private Double match;

    private Double distance;

    private Location location;

    public UserDTO withMatch(Double match) {
        this.match = match;
        return this;
    }

    public UserDTO withDistance(Double distance) {
        this.distance = distance;
        return this;
    }

    public UserDTO withLocation(Location location) {
        this.location = location;
        return this;
    }

    public boolean isNearby(UserDTO user) {
        return Math.abs(this.getCoordinates().getLongitude() - user.getCoordinates().getLongitude()) < 0.3
                && Math.abs(this.getCoordinates().getLatitude() - user.getCoordinates().getLatitude()) < 0.3;
    }
}
