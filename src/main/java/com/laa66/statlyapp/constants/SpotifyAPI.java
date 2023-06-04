package com.laa66.statlyapp.constants;

public enum SpotifyAPI {

    TOKEN_ENDPOINT("https://accounts.spotify.com/api/token"),
    CURRENT_USER("https://api.spotify.com/v1/me"),
    RECENTLY_PLAYED_TRACKS("https://api.spotify.com/v1/me/player/recently-played?limit=50"),
    TOP_ARTISTS("https://api.spotify.com/v1/me/top/artists?limit=50&time_range="),
    TOP_TRACKS("https://api.spotify.com/v1/me/top/tracks?limit=50&time_range="),
    CREATE_TOP_PLAYLIST("https://api.spotify.com/v1/users/user_id/playlists"),
    ADD_PLAYLIST_TRACK("https://api.spotify.com/v1/playlists/playlist_id/tracks"),
    EDIT_PLAYLIST_IMAGE("https://api.spotify.com/v1/playlists/playlist_id/images"),
    TRACKS_ANALYSIS("https://api.spotify.com/v1/audio-features?ids="),
    USER_PLAYLISTS("https://api.spotify.com/v1/users/user_id/playlists?offset=offset_num&limit=50"),

    PLAYLIST_RANGE_SHORT("last 4 weeks"),
    PLAYLIST_RANGE_MEDIUM("last 6 months"),
    PLAYLIST_RANGE_LONG("all-time");

    private final String text;

    SpotifyAPI(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
