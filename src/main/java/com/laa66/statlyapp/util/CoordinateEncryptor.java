package com.laa66.statlyapp.util;

import com.laa66.statlyapp.model.mapbox.Coordinates;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

import java.util.stream.Stream;

// solve case when coordinate is negative
public class CoordinateEncryptor {

    /*
        When using encrypting function positive
        coordinate is counted as '1'
        in returned coordinates pair
     */
    public static Pair<String, String> encrypt(Coordinates coordinates) {
        Double lon = coordinates.getLongitude();
        Double lat = coordinates.getLatitude();
        String[] encryptedCoordinates = Stream.of(lon, lat)
                .map(aDouble -> {
                    int index = aDouble.toString().indexOf(".");
                    boolean isNegative = aDouble < 0;
                    String toModify = aDouble.toString();
                    if (isNegative) {
                        toModify = toModify.replaceFirst("-", "");
                        index--;
                    }
                    return new StringBuilder(toModify)
                            .deleteCharAt(index)
                            .append(index)
                            .append(isNegative ? 0 : 1)
                            .reverse()
                            .toString();
                })
                .toArray(String[]::new);

        return Pair.of(encryptedCoordinates[0], encryptedCoordinates[1]);
    }

    public static Coordinates decrypt(Pair<String, String> encryptedCoordinates) {
        String lon = StringUtils.reverse(encryptedCoordinates.getFirst());
        String lat = StringUtils.reverse(encryptedCoordinates.getSecond());
        double[] array = Stream.of(lon, lat)
                .mapToDouble(coordinate -> {
                    int len = coordinate.length();
                    return Double.parseDouble(new StringBuilder(coordinate)
                            .delete(len - 2, len)
                            .insert(Integer.parseInt(String.valueOf(coordinate.charAt(len - 2))), ".")
                            .insert(0, Integer.parseInt(String.valueOf(coordinate.charAt(len - 1))) == 0 ? "-" : "")
                            .toString());
                }).toArray();

        return new Coordinates(array[0], array[1]);
    }
}
