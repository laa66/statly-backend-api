package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.constants.MapboxAPI;
import com.laa66.statlyapp.model.mapbox.Coordinates;
import com.laa66.statlyapp.model.mapbox.Location;
import com.laa66.statlyapp.model.mapbox.Matrix;
import com.laa66.statlyapp.service.MapAPIService;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@AllArgsConstructor
public class MapboxMapAPIService implements MapAPIService {

    private final String accessToken;
    private final RestTemplate restTemplate;

    @Override
    public Collection<UserDTO> getDistanceMatrix(List<UserDTO> users) {
        Matrix matrix = restTemplate.getForObject(prepareDistanceMatrixUrl(users), Matrix.class);
        return Optional.ofNullable(matrix)
                .map(Matrix::getDistances)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(dist -> IntStream.range(0, dist.size())
                        .mapToObj(i -> users.get(i).withDistance(dist.get(i)))
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public UserDTO getReverseGeocoding(UserDTO user) {
        Location response = restTemplate.getForObject(prepareReverseGeocodingUrl(user), Location.class);
        return Optional.ofNullable(response)
                .map(user::withLocation)
                .orElse(user);
    }

    // helpers
    private String prepareDistanceMatrixUrl(List<UserDTO> users) {
        String coordinates = StringUtils.collectionToDelimitedString(users.stream()
                .map(userDTO -> userDTO.getCoordinates().getLongitude()
                        + "," + userDTO.getCoordinates().getLatitude())
                .toList(), ";");
        return UriComponentsBuilder.fromUriString(MapboxAPI.DISTANCE_MATRIX.get())
                .replaceQueryParam("access_token", accessToken)
                .buildAndExpand(coordinates)
                .toUriString();
    }

    private String prepareReverseGeocodingUrl(UserDTO user) {
        Coordinates coordinates = user.getCoordinates();
        return UriComponentsBuilder.fromUriString(MapboxAPI.REVERSE_GEOCODING.get())
                .replaceQueryParam("access_token", accessToken)
                .buildAndExpand(coordinates.getLongitude() + "," + coordinates.getLatitude())
                .toUriString();
    }
}
