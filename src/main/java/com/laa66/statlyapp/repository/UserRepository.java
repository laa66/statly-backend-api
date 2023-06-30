package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u from User u WHERE u.username LIKE %:username%")
    Collection<User> findAllMatchingUsers(@Param("username") String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

}
