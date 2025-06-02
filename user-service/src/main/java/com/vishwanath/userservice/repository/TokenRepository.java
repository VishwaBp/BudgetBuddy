package com.vishwanath.userservice.repository;

import com.vishwanath.userservice.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    List<Token> findAllByUserId(Integer userId);

    Optional<Token> findByToken(String token);
}
