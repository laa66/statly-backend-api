package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserTrack;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TrackRepository extends CrudRepository<UserTrack, Long> {
    Optional<UserTrack> findFirstByUserIdAndRangeOrderByDateDesc(long userId, String range);

}
