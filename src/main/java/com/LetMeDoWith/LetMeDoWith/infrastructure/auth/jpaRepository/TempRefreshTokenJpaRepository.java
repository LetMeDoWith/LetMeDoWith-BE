package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.infrastructure.auth.entity.TempRefreshToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRefreshTokenJpaRepository extends JpaRepository<TempRefreshToken, String> {
  void deleteByToken(String token);

  void deleteByMemberId(String memberId);
}
