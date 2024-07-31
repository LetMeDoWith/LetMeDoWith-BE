package com.LetMeDoWith.LetMeDoWith.infrastructure.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.LetMeDoWith.LetMeDoWith.application.member.domain.Member;
import com.LetMeDoWith.LetMeDoWith.application.member.domain.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.application.member.domain.MemberTermAgree;
import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberAgreementCommand;
import com.LetMeDoWith.LetMeDoWith.application.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberSocialAccountJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberTermAgreeJpaRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberJpaRepository memberJpaRepository;
	private final MemberTermAgreeJpaRepository termAgreeJpaRepository;
	private final MemberSocialAccountJpaRepository socialAccountJpaRepository;

	@Override
	public Optional<Member> getMember(Long id, MemberStatus memberStatus) {
		return memberJpaRepository.findByIdAndStatus(id, memberStatus);
	}

	@Override
	public Optional<Member> getMember(SocialProvider provider, String subject) {
		return memberJpaRepository.findByProviderAndSubject(provider, subject);
	}

	@Override
	public List<Member> getMembers(List<MemberStatus> memberStatuses) {
		return memberJpaRepository.findAllByStatusIn(memberStatuses);
	}

	@Override
	public Member save(Member member) {
		return memberJpaRepository.save(member);
	}

	@Override
	public void saveAgreement(Member member, MemberAgreementCommand command) {

		if (!command.isTermsAgree() || !command.isPrivacyAgree()) {
			throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
		}

		termAgreeJpaRepository.save(
			MemberTermAgree.initialize(member, command.isTermsAgree(), command.isPrivacyAgree(), command.isAdvertisementAgree())
		);

		memberJpaRepository.save(member);

	}

	@Override
	public void saveSocialAccount(MemberSocialAccount memberSocialAccount) {
		socialAccountJpaRepository.save(memberSocialAccount);
	}
}