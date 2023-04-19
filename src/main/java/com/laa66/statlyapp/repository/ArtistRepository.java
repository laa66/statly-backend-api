package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserArtist;
import org.springframework.data.repository.CrudRepository;

public interface ArtistRepository extends CrudRepository<UserArtist, Long> {

}
