package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserGenre;
import org.springframework.data.repository.CrudRepository;

public interface GenreRepository extends CrudRepository<UserGenre, Long> {

}
