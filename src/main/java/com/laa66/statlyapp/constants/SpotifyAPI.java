package com.laa66.statlyapp.constants;

public enum SpotifyAPI {

    RECENTLY_PLAYED_TRACKS("https://api.spotify.com/v1/me/player/recently-played?limit=50");

    String url;

    SpotifyAPI(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
