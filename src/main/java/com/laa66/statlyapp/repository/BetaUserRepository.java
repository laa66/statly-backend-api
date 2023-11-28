package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.BetaUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BetaUserRepository extends CrudRepository<BetaUser, Long> {
    boolean existsByEmail(String email);
    Optional<BetaUser> findByEmail(String email);
}
