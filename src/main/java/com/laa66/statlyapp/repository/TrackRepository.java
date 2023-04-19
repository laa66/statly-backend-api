package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserTrack;
import org.springframework.data.repository.CrudRepository;

public interface TrackRepository extends CrudRepository<UserTrack, Long> {


}
