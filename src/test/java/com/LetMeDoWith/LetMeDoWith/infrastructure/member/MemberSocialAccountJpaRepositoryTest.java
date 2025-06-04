package com.LetMeDoWith.LetMeDoWith.infrastructure.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.task.TaskCompleteLevel;
import com.LetMeDoWith.LetMeDoWith.config.JpaAuditingConfiguration;
import com.LetMeDoWith.LetMeDoWith.config.TestQueryDslConfig;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberSocialAccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({JpaAuditingConfiguration.class, TestQueryDslConfig.class})
class MemberSocialAccountJpaRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberSocialAccountJpaRepository memberSocialAccountJpaRepository;

    @BeforeEach
    void beforeEach() {
        entityManager.clear();

        Member testMemberObj =
                Member.builder()
                        .subject("test@email.com")
                        .nickname("nickname")
                        .selfDescription("self desc")
                        .status(MemberStatus.NORMAL)
                        .type(MemberType.USER)
                        .profileImageUrl("image.jpeg")
                        .taskCompleteLevel(TaskCompleteLevel.AVERAGE)
                        .build();

        memberJpaRepository.save(testMemberObj);
    }

    @Test
    @DisplayName("[SUCCESS] MemberSocialAccount 생성 및 조회")
    void InsertNewMemberSocialAccountEntityTest() {
        Optional<Member> memberOptional = memberJpaRepository.findBySubject("test@email.com");
        Member member = memberOptional.get();

        MemberSocialAccount memberSocialAccountKaKao =
                MemberSocialAccount.builder().member(member).provider(SocialProvider.KAKAO).build();

        MemberSocialAccount memberSocialAccountGoogle =
                MemberSocialAccount.builder().member(member).provider(SocialProvider.GOOGLE).build();

        memberSocialAccountJpaRepository.save(memberSocialAccountKaKao);
        memberSocialAccountJpaRepository.save(memberSocialAccountGoogle);

        Optional<MemberSocialAccount> kakaoAccount =
                memberSocialAccountJpaRepository.findByMemberAndProvider(member, SocialProvider.KAKAO);

        Optional<MemberSocialAccount> googleAccount =
                memberSocialAccountJpaRepository.findByMemberAndProvider(member, SocialProvider.GOOGLE);

        assertEquals(kakaoAccount.get().getMember().getNickname(), "nickname");
        assertEquals(googleAccount.get().getMember().getNickname(), "nickname");
        assertEquals(kakaoAccount.get().getMember(), googleAccount.get().getMember());
    }
}
