package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
