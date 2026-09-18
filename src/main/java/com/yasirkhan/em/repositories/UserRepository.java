package com.yasirkhan.em.repositories;

import com.yasirkhan.em.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN u.employee e WHERE u.username = :identifier OR e.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
}
