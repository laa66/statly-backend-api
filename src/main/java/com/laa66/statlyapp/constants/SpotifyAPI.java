package com.laa66.statlyapp.constants;

public class SpotifyAPI {

    // GET current user info
    public final static String CURRENT_USER = "https://api.spotify.com/v1/me";

    // GET recently played tracks endpoint
    public final static String RECENTLY_PLAYED_TRACKS = "https://api.spotify.com/v1/me/player/recently-played?limit=50";

    // GET top artists endpoints
    public final static String TOP_ARTISTS = "https://api.spotify.com/v1/me/top/artists?limit=50&time_range=";;

    // GET top tracks endpoints
    public final static String TOP_TRACKS = "https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=";

    // POST top tracks endpoints
    public final static String CREATE_TOP_PLAYLIST = "https://api.spotify.com/v1/users/user_id/playlists";
    public final static String ADD_PLAYLIST_TRACK = "https://api.spotify.com/v1/playlists/playlist_id/tracks";

    // POST top tracks ranges
    public final static String RANGE_SHORT = "last 4 weeks";
    public final static String RANGE_MEDIUM = "last 6 months";
    public final static String RANGE_LONG = "all time";

}
