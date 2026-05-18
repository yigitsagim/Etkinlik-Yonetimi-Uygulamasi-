package com.works.etkinlikyonetimiuygulamasi.repository;

import com.works.etkinlikyonetimiuygulamasi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailEqualsIgnoreCase(String email);
    Optional<Users> findByUsernameEqualsIgnoreCase(String username);
}