package com.laa66.statlyapp.constants;

public enum MapboxMatrixAPI {

    DISTANCE_MATRIX("https://api.mapbox.com/directions-matrix/v1/mapbox/walking/{coordinates_list}?annotations=distance,duration&sources=0&access_token={access_token}");

    private final String text;

    MapboxMatrixAPI(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
