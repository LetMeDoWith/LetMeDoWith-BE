// package com.LetMeDoWith.LetMeDoWith.infrastructure.member;
//
// import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
// import com.LetMeDoWith.LetMeDoWith.domain.member.dto.MemberBadgeVO;
// import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
// import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
// import com.LetMeDoWith.LetMeDoWith.config.TestQueryDslConfig;
// import com.LetMeDoWith.LetMeDoWith.domain.member.model.Badge;
// import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberBadge;
// import
// com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.BadgeJpaRepository;
// import
// com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberBadgeJpaRepository;
// import java.util.List;
// import lombok.extern.slf4j.Slf4j;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import
// org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import
// org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// import org.springframework.context.annotation.Import;
//
// @DataJpaTest
// @AutoConfigureTestDatabase(replace = Replace.NONE)
// @Slf4j
// @Import(TestQueryDslConfig.class)
// public class BadgeJpaRepositoryTest {
//
// @Autowired
// private TestEntityManager entityManager;
//
// @Autowired
// private BadgeJpaRepository badgeJpaRepository;
//
// @Autowired
// private MemberBadgeJpaRepository memberBadgeJpaRepository;
//
// @BeforeEach
// void beforeEach() { entityManager.clear(); }
//
// static BadgeStatus badge1Status = BadgeStatus.ACTIVE;
// static String badge1Name = "뱃지1";
// static String badge1Description = "뱃지1 description";
// static String badge1ActiveHint = "뱃지1 힌트";
// static String badge1ImageUrl = "뱃지1 이미지 url";
// static int badge1SortOrder = 2;
//
// static BadgeStatus badge2Status = BadgeStatus.ACTIVE;
// static String badge2Name = "뱃지2";
// static String badge2Description = "뱃지2 description";
// static String badge2ActiveHint = "뱃지2 힌트";
// static String badge2ImageUrl = "뱃지2 이미지 url";
// static int badge2SortOrder = 1;
//
// static BadgeStatus badge3Status = BadgeStatus.ACTIVE;
// static String badge3Name = "뱃지3";
// static String badge3Description = "뱃지3 description";
// static String badge3ActiveHint = "뱃지3 힌트";
// static String badge3ImageUrl = "뱃지3 이미지 url";
// static int badge3SortOrder = 3;
//
// @Test
// @DisplayName("badge save 테스트")
// void test_badge_save() {
// // given
// Badge badge1 = Badge.builder()
// .badgeStatus(badge1Status)
// .name(badge1Name)
// .description(badge1Description)
// .acquireHint(badge1ActiveHint)
// .imageUrl(badge1ImageUrl)
// .sortOrder(badge1SortOrder)
// .build();
//
// Badge badge2 = Badge.builder()
// .badgeStatus(badge2Status)
// .name(badge2Name)
// .description(badge2Description)
// .acquireHint(badge2ActiveHint)
// .imageUrl(badge2ImageUrl)
// .sortOrder(badge2SortOrder)
// .build();
//
// Badge badge3 = Badge.builder()
// .badgeStatus(badge3Status)
// .name(badge3Name)
// .description(badge3Description)
// .acquireHint(badge3ActiveHint)
// .imageUrl(badge3ImageUrl)
// .sortOrder(badge3SortOrder)
// .build();
//
// // when
// badgeJpaRepository.saveAll(List.of(badge1, badge2, badge3));
// badgeJpaRepository.flush();
// Badge savedBadge1 = badgeJpaRepository.findByName(badge1Name).orElseThrow(()
// -> new
// IllegalArgumentException("not found"));
// Badge savedBadge2 = badgeJpaRepository.findByName(badge2Name).orElseThrow(()
// -> new
// IllegalArgumentException("not found"));
// Badge savedBadge3 = badgeJpaRepository.findByName(badge3Name).orElseThrow(()
// -> new
// IllegalArgumentException("not found"));
//
// // then
// assertThat(savedBadge1.getId()).isNotNull();
// assertThat(savedBadge1.getDescription()).isEqualTo(badge1Description);
// assertThat(savedBadge2.getId()).isNotNull();
// assertThat(savedBadge2.getDescription()).isEqualTo(badge2Description);
// assertThat(savedBadge3.getId()).isNotNull();
// assertThat(savedBadge3.getDescription()).isEqualTo(badge3Description);
//
// }
//
// @Test()
// @DisplayName("memberBadge save 테스트")
// void test_memberBadge_save() {
// // given
// Badge badge1 = Badge.builder()
// .badgeStatus(badge1Status)
// .name(badge1Name)
// .description(badge1Description)
// .acquireHint(badge1ActiveHint)
// .imageUrl(badge1ImageUrl)
// .sortOrder(badge1SortOrder)
// .build();
// Badge savedBadge = badgeJpaRepository.save(badge1);
//
// MemberBadge memberBadge1 = MemberBadge.builder()
// .memberId(1L)
// .badge(savedBadge)
// .isMain(Yn.TRUE)
// .build();
//
// MemberBadge memberBadge2 = MemberBadge.builder()
// .memberId(1L)
// .badge(savedBadge)
// .isMain(Yn.FALSE)
// .build();
//
// // when
// memberBadgeJpaRepository.saveAll(List.of(memberBadge1, memberBadge2));
// memberBadgeJpaRepository.flush();
// MemberBadge savedMemberBadge1 =
// memberBadgeJpaRepository.findByMemberIdAndIsMain(1L, Yn.TRUE)
// .orElseThrow(() -> new IllegalArgumentException("not found"));
// MemberBadge savedMemberBadge2 =
// memberBadgeJpaRepository.findByMemberIdAndIsMain(1L, Yn.FALSE)
// .orElseThrow(() -> new IllegalArgumentException("not found"));
//
// // then
// assertThat(savedMemberBadge1.getId()).isNotNull();
// assertThat(savedMemberBadge1.getMemberId()).isEqualTo(1L);
// assertThat(savedMemberBadge1.getIsMain()).isEqualTo(Yn.TRUE);
// assertThat(savedMemberBadge1.getBadge().getId()).isEqualTo(savedBadge.getId());
//
// assertThat(savedMemberBadge2.getId()).isNotNull();
// assertThat(savedMemberBadge2.getMemberId()).isEqualTo(1L);
// assertThat(savedMemberBadge2.getIsMain()).isEqualTo(Yn.FALSE);
// assertThat(savedMemberBadge2.getBadge().getId()).isEqualTo(savedBadge.getId());
//
// }
//
// @Test
// @DisplayName("findAllByMemberIdAndBadgeStatus 테스트")
// void test_findAllByMemberIdAndBadgeStatus() {
// // given
// Badge badge1 = badgeJpaRepository.save(Badge.builder()
// .badgeStatus(badge1Status)
// .name(badge1Name)
// .description(badge1Description)
// .acquireHint(badge1ActiveHint)
// .imageUrl(badge1ImageUrl)
// .sortOrder(badge1SortOrder)
// .build());
//
// Badge badge2 = badgeJpaRepository.save(Badge.builder()
// .badgeStatus(badge2Status)
// .name(badge2Name)
// .description(badge2Description)
// .acquireHint(badge2ActiveHint)
// .imageUrl(badge2ImageUrl)
// .sortOrder(badge2SortOrder)
// .build());
//
// Badge badge3 = badgeJpaRepository.save(Badge.builder()
// .badgeStatus(badge3Status)
// .name(badge3Name)
// .description(badge3Description)
// .acquireHint(badge3ActiveHint)
// .imageUrl(badge3ImageUrl)
// .sortOrder(badge3SortOrder)
// .build());
//
// MemberBadge memberBadge1 =
// memberBadgeJpaRepository.save(MemberBadge.builder()
// .memberId(1L)
// .badge(badge1)
// .isMain(Yn.TRUE)
// .build());
//
// MemberBadge memberBadge2 =
// memberBadgeJpaRepository.save(MemberBadge.builder()
// .memberId(1L)
// .badge(badge2)
// .isMain(Yn.FALSE)
// .build());
//
// // when
// List<MemberBadgeVO> result = badgeJpaRepository.findAllJoinMemberBadge(1L,
// BadgeStatus.ACTIVE);
//
// // then
// assertThat(result.size()).isEqualTo(3);
//
// assertThat(result.get(0).getName()).isEqualTo(badge2Name);
// assertThat(result.get(1).getName()).isEqualTo(badge1Name);
// assertThat(result.get(2).getName()).isEqualTo(badge3Name);
//
// }
//
// }
