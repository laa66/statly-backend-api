package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.constants.MapboxMatrixAPI;
import com.laa66.statlyapp.model.mapbox.Matrix;
import com.laa66.statlyapp.service.MatrixAPIService;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@AllArgsConstructor
public class MapboxMatrixAPIService implements MatrixAPIService {

    private final String accessToken;
    private final RestTemplate restTemplate;

    @Override
    public Collection<UserDTO> getDistanceMatrix(List<UserDTO> users) {
        Matrix matrix = restTemplate.getForObject(prepareUrl(users), Matrix.class);
        return Optional.ofNullable(matrix)
                .map(Matrix::getDistances)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(dist -> IntStream.range(0, dist.size())
                        .mapToObj(i -> {
                            users.get(i).withDistance(dist.get(i));
                            return users.get(i);
                        }).toList())
                .orElse(Collections.emptyList());
    }

    private String prepareUrl(List<UserDTO> users) {
        String coordinates = StringUtils.collectionToDelimitedString(users.stream()
                .map(userDTO -> userDTO.getCoordinates().getLongitude()
                        + "," + userDTO.getCoordinates().getLatitude())
                .toList(), ";");
        return MapboxMatrixAPI.DISTANCE_MATRIX.get()
                .replace("{coordinates_list}", coordinates)
                .replace("{access_token}", accessToken);
    }
}
