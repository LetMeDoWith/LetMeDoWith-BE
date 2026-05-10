package com.LetMeDoWith.LetMeDoWith.domain.member.repository;

import com.LetMeDoWith.LetMeDoWith.domain.member.repository.dto.MemberDowithQueryDto;
import java.util.Optional;

public interface MemberQueryRepository {

    Optional<MemberDowithQueryDto> getMyDowithInfo(String memberId);
}
