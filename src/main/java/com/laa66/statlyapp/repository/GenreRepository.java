package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserGenre;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface GenreRepository extends CrudRepository<UserGenre, Long> {
    Optional<UserGenre> findFirstByUserIdAndRangeOrderByDateDesc(long userId, String range);
    Collection<UserGenre> findAllByUserId(long userId);
}
