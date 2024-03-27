package com.LetMeDoWith.LetMeDoWith.repository.auth;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.LetMeDoWith.LetMeDoWith.entity.auth.RefreshToken;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

}
