package com.laa66.statlyapp.constants;

public enum MapboxAPI {

    DISTANCE_MATRIX("https://api.mapbox.com/directions-matrix/v1/mapbox/walking/{coordinates_list}?annotations=distance,duration&sources=0&access_token={access_token}"),
    REVERSE_GEOCODING("https://api.mapbox.com/geocoding/v5/mapbox.places/{coordinates_list}.json?types=place,country&access_token={access_token}");

    private final String text;

    MapboxAPI(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
