package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserMainstream;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MainstreamRepository extends CrudRepository<UserMainstream, Long> {
    Optional<UserMainstream> findFirstByUserIdAndRangeOrderByDateDesc(long userId, String range);
}
