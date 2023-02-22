package com.laa66.statlyapp.constants;

public class SpotifyAPI {

    public final static String RECENTLY_PLAYED_TRACKS = "https://api.spotify.com/v1/me/player/recently-played?limit=50";

    public final static String TOP_ARTISTS_SHORT = "https://api.spotify.com/v1/me/top/artists?limit=50&time_range=short_term";
    public final static String TOP_ARTISTS_MEDIUM = "https://api.spotify.com/v1/me/top/artists?limit=50&time_range=medium_term";
    public final static String TOP_ARTISTS_LONG = "https://api.spotify.com/v1/me/top/artists?limit=50&time_range=long_term";

    public final static String TOP_TRACKS_SHORT = "https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=short_term";
    public final static String TOP_TRACKS_MEDIUM = "https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=medium_term";
    public final static String TOP_TRACKS_LONG = "https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=long_term";
}
