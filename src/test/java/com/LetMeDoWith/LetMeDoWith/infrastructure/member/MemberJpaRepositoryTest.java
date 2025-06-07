package com.LetMeDoWith.LetMeDoWith.infrastructure.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.config.TestQueryDslConfig;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberSocialAccountJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
@Import(TestQueryDslConfig.class)
class MemberJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private MemberSocialAccountJpaRepository memberSocialAccountJpaRepository;

    private static Member getMember() {
        Member testMemberObj =
                Member.builder()
                        .id("01HXQ2X7Z7Q6XJX4X2X7Z7Q6XA")
                        .subject("test@email.com")
                        .nickname("nickname")
                        .selfDescription("self desc")
                        .status(MemberStatus.NORMAL)
                        .type(MemberType.USER)
                        .profileImageUrl("image.jpeg")
                        .build();
        return testMemberObj;
    }

    @BeforeEach
    void beforeEach() {
        entityManager.clear();
    }

    @Test
    @DisplayName("새로운 객체 삽입 테스트")
    void test_insert_new() {
        Member testMemberObj = getMember();

        memberJpaRepository.save(testMemberObj);

        Optional<Member> user = memberJpaRepository.findByNickname("nickname");

        assertInstanceOf(Member.class, user.get());
        assertEquals(user.get().getNickname(), "nickname");
        assertEquals(user.get().getStatus(), MemberStatus.NORMAL);
    }

    @Test
    @DisplayName("객체 삭제 테스트")
    void test_delete() {
        Member testMemberObj = getMember();

        memberJpaRepository.save(testMemberObj);
        memberJpaRepository.flush();

        Optional<Member> user = memberJpaRepository.findByNickname("nickname");

        assertInstanceOf(Member.class, user.get());
        assertEquals(user.get().getNickname(), "nickname");

        // delete
        memberJpaRepository.deleteAll();
        Optional<Member> userAfterDelete = memberJpaRepository.findByNickname("nickname");

        Assertions.assertThrows(NoSuchElementException.class, userAfterDelete::get);
    }

    @Test
    void test_duplicated_key() {

        Member testMemberObj = getMember();

        Member testMemberObjWithoutKey =
                Member.builder()
                        .subject("test@email.com")
                        .nickname("nickname2")
                        .selfDescription("self desc")
                        .status(MemberStatus.NORMAL)
                        .type(MemberType.USER)
                        .profileImageUrl("image.jpeg")
                        .build();

        memberJpaRepository.save(testMemberObj);
        memberJpaRepository.save(testMemberObjWithoutKey);

        Optional<Member> userWithCustomKey = memberJpaRepository.findByNickname("nickname");
        Optional<Member> userWithDefaultKey = memberJpaRepository.findByNickname("nickname2");

        assertNotEquals(testMemberObj.getId(), testMemberObjWithoutKey.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 가입된 유저 조회 테스트")
    void findRegisteredMemberTest() {

        Member testMemberObj = getMember();

        Member member = memberJpaRepository.save(testMemberObj);

        MemberSocialAccount memberSocialAccount =
                MemberSocialAccount.builder().provider(SocialProvider.KAKAO).member(member).build();

        memberSocialAccountJpaRepository.save(memberSocialAccount);

        Optional<Member> optionalMember =
                memberJpaRepository.findByProviderAndSubject(SocialProvider.KAKAO, "test@email.com");
        log.debug("member= {}", optionalMember.get().getNickname());
        assertNotNull(optionalMember);
        assertEquals(optionalMember.get().getNickname(), "nickname");
    }
}
