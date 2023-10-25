package com.laa66.statlyapp.service;

public interface LibraryDataSyncService {

    default void synchronize() {
        synchronizeTracks();
        synchronizeArtists();
        synchronizeGenres();
    }

    void synchronizeTracks();

    void synchronizeArtists();

    void synchronizeGenres();

}
