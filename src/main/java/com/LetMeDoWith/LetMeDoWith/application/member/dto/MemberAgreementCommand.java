package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import lombok.Builder;

@Builder
public record MemberAgreementCommand(boolean isTermsAgree, boolean isPrivacyAgree, boolean isAdvertisementAgree) {}
