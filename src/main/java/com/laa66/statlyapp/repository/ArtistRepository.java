package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserArtist;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface ArtistRepository extends CrudRepository<UserArtist, Long> {
    Optional<UserArtist> findFirstByUserIdAndRangeOrderByDateDesc(long userId, String range);
    Collection<UserArtist> findAllByUserId(long userId);
}
