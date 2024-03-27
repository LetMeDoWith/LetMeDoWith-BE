package com.LetMeDoWith.LetMeDoWith.repository.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.entity.member.Member;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private MemberRepository memberRepository;
    
    
    @Test
    @DisplayName("새로운 객체 삽입 테스트")
    void test_insert_new() {
        Member testMemberObj = Member.builder()
                                     .email("test@email.com")
                                     .nickname("nickname")
                                     .selfDescription("self desc")
                                     .status(MemberStatus.NORMAL)
                                     .type(MemberType.USER)
                                     .profileImageUrl("image.jpeg")
                                     .marketingTermAgreeYn(true)
                                     .build();
        
        memberRepository.save(testMemberObj);
        
        Optional<Member> user = memberRepository.findByNickname("nickname");
        
        assertInstanceOf(Member.class, user.get());
        assertEquals(user.get().getNickname(), "nickname");
        assertEquals(user.get().getStatus(), MemberStatus.NORMAL);
    }
    
    @Test
    @DisplayName("객체 삭제 테스트")
    void test_delete() {
        Member testMemberObj = Member.builder()
                                     .email("test@email.com")
                                     .nickname("nickname")
                                     .selfDescription("self desc")
                                     .status(MemberStatus.NORMAL)
                                     .type(MemberType.USER)
                                     .profileImageUrl("image.jpeg")
                                     .marketingTermAgreeYn(true)
                                     .build();
        
        memberRepository.save(testMemberObj);
        
        Optional<Member> user = memberRepository.findByNickname("nickname");
        
        assertInstanceOf(Member.class, user.get());
        assertEquals(user.get()
                         .getNickname(), "nickname");
        
        // delete
        memberRepository.deleteAll();
        Optional<Member> userAfterDelete = memberRepository.findByNickname("nickname");
        
        Assertions.assertThrows(NoSuchElementException.class, userAfterDelete::get);
    }
    
    @Test
    void test_duplicated_key() {
        Member testMemberObj = Member.builder()
                                     .id(1L)
                                     .email("test@email.com")
                                     .nickname("nickname")
                                     .selfDescription("self desc")
                                     .status(MemberStatus.NORMAL)
                                     .type(MemberType.USER)
                                     .profileImageUrl("image.jpeg")
                                     .marketingTermAgreeYn(true)
                                     .build();
        
        Member testMemberObjWithoutKey = Member.builder()
                                               .email("test@email.com")
                                               .nickname("nickname2")
                                               .selfDescription("self desc")
                                               .status(MemberStatus.NORMAL)
                                               .type(MemberType.USER)
                                               .profileImageUrl("image.jpeg")
                                               .marketingTermAgreeYn(true)
                                               .build();
        
        memberRepository.save(testMemberObj);
        memberRepository.save(testMemberObjWithoutKey);
        
        Optional<Member> userWithCustomKey = memberRepository.findByNickname("nickname");
        Optional<Member> userWithDefaultKey = memberRepository.findByNickname("nickname2");
        
        assertEquals(userWithCustomKey.get().getId(), 1L);
        assertEquals(userWithDefaultKey.get().getId(), 2L);
    }
}