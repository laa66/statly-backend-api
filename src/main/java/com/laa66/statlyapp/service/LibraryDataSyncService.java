package com.laa66.statlyapp.service;

public interface LibraryDataSyncService {

    default void synchronize(long userId) {
        synchronizeTracks(userId);
        synchronizeArtists(userId);
        synchronizeGenres(userId);
    }

    void synchronizeTracks(long userId);

    void synchronizeArtists(long userId);

    void synchronizeGenres(long userId);

    boolean isLibraryDataSynchronized(long userId);

}
