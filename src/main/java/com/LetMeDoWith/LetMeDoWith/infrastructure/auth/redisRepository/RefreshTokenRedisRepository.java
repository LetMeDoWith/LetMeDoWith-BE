package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.redisRepository;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

    void deleteByMemberId(Long memberId);
}
