package com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, String>, QMemberJpaRepository {

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByIdAndStatus(String id, MemberStatus status);

    Optional<Member> findBySubject(String subject);

    List<Member> findAllByStatusIn(List<MemberStatus> memberStatuses);

    List<Member> findAllByNicknameAndStatusIn(String nickname, List<MemberStatus> memberStatuses);
}
