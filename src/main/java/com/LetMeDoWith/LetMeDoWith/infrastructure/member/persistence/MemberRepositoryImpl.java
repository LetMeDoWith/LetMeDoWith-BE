package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberTermAgree;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberSocialAccountJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberTermAgreeJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberTermAgreeJpaRepository termAgreeJpaRepository;
    private final MemberSocialAccountJpaRepository socialAccountJpaRepository;

    @Override
    public Optional<Member> getMember(String id, MemberStatus memberStatus) {
        return memberJpaRepository.findByIdAndStatus(id, memberStatus);
    }

    @Override
    public Optional<Member> getMember(SocialProvider provider, String subject) {
        return memberJpaRepository.findByProviderAndSubject(provider, subject);
    }

    @Override
    public Optional<Member> getNormalStatusMember(String id) {
        return memberJpaRepository.findByIdAndStatus(id, MemberStatus.NORMAL);
    }

    @Override
    public List<Member> getMembers(String nickname, List<MemberStatus> memberStatuses) {
        return memberJpaRepository.findAllByNicknameAndStatusIn(nickname, memberStatuses);
    }

    @Override
    public Member save(Member member) {
        if (member.getTermAgree() != null) {
            termAgreeJpaRepository.save(member.getTermAgree());
        }

        return memberJpaRepository.save(member);
    }

    @Override
    public MemberTermAgree save(MemberTermAgree memberTermAgree) {

        if (!memberTermAgree.isTermsOfAgree() || !memberTermAgree.isPrivacy()) {
            throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
        }

        return termAgreeJpaRepository.save(memberTermAgree);
    }

    @Override
    public void saveSocialAccount(MemberSocialAccount memberSocialAccount) {
        socialAccountJpaRepository.save(memberSocialAccount);
    }
}
