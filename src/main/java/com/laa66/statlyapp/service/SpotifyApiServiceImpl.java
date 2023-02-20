package com.laa66.statlyapp.service;

import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpotifyApiServiceImpl implements SpotifyApiService {

    @Override
    public List<Track> getTopTracks() {
        return null;
    }

    @Override
    public List<Artist> getTopArtists() {
        return null;
    }

 /*
    @Override
    public List<Genre> getTopGenres() {
        return null;
    }
  */

    @Override
    public List<Track> getRecentlyPlayed() {
        return null;
    }

    @Override
    public void postPlaylist() {

    }
}
