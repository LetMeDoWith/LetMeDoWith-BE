-- V0.0.2__insert_member_domain_test_data.sql
-- Flyway 마이그레이션: 멤버 유관 테이블 테스트 데이터 삽입
-- member_social_account, member_term_agree, member_alarm_setting 테이블에 테스트 데이터 추가

-- member_social_account 테이블 데이터 삽입
-- 멤버들에게 다양한 소셜 로그인 계정 할당

INSERT INTO member (id, created_at, updated_at, created_by, updated_by, subject, status,
                    nickname, self_description,
                    type, profile_image_url)
VALUES ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XA', NOW(), NOW(), 'admin', 'admin', '1810473492', 'NORMAL', 'a1', 'a1 자기소개',
        'USER',
        's3:://a1_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XB', NOW(), NOW(), 'admin', 'admin', '2810473493', 'NORMAL', 'a2', 'a2 자기소개',
        'USER',
        's3:://a2_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XC', NOW(), NOW(), 'admin', 'admin', '3810473494', 'NORMAL', 'a3', 'a3 자기소개',
        'USER',
        's3:://a3_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XD', NOW(), NOW(), 'admin', 'admin', '4810473495', 'NORMAL', 'a4', 'a4 자기소개',
        'USER',
        's3:://a4_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XE', NOW(), NOW(), 'admin', 'admin', '5810473496', 'NORMAL', 'a5', 'a5 자기소개',
        'USER',
        's3:://a5_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XF', NOW(), NOW(), 'admin', 'admin', '6810473497', 'NORMAL', 'a6', 'a6 자기소개',
        'USER',
        's3:://a6_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XG', NOW(), NOW(), 'admin', 'admin', '7810473498', 'NORMAL', 'a7', 'a7 자기소개',
        'USER',
        's3:://a7_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XH', NOW(), NOW(), 'admin', 'admin', '8810473499', 'NORMAL', 'a8', 'a8 자기소개',
        'USER',
        's3:://a8_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XI', NOW(), NOW(), 'admin', 'admin', '9810473490', 'NORMAL', 'a9', 'a9 자기소개',
        'USER',
        's3:://a9_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XJ', NOW(), NOW(), 'admin', 'admin', '9110473491', 'NORMAL', 'a10', 'a10 자기소개',
        'USER',
        's3:://a10_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XK', NOW(), NOW(), 'admin', 'admin', '9210473492', 'NORMAL', 'a11', 'a11 자기소개',
        'USER',
        's3:://a11_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XL', NOW(), NOW(), 'admin', 'admin', '9310473493', 'NORMAL', 'a12', 'a12 자기소개',
        'USER',
        's3:://a12_profile_image');

-- member_follow 테이블 데이터 삽입
INSERT INTO member_follow (created_at, updated_at, created_by, updated_by, follower_id, following_id)
VALUES (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a1'),
        (SELECT id FROM member WHERE nickname = 'a2')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a1'),
        (SELECT id FROM member WHERE nickname = 'a3')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a1'),
        (SELECT id FROM member WHERE nickname = 'a4')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a1'),
        (SELECT id FROM member WHERE nickname = 'a5')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a1'),
        (SELECT id FROM member WHERE nickname = 'a6'));

INSERT INTO member_follow (created_at, updated_at, created_by, updated_by, follower_id, following_id)
VALUES (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a7'),
        (SELECT id FROM member WHERE nickname = 'a1')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a8'),
        (SELECT id FROM member WHERE nickname = 'a1')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a9'),
        (SELECT id FROM member WHERE nickname = 'a1')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a10'),
        (SELECT id FROM member WHERE nickname = 'a1')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a11'),
        (SELECT id FROM member WHERE nickname = 'a1')),
       (NOW(), NOW(), 'admin', 'admin', (SELECT id FROM member WHERE nickname = 'a12'),
        (SELECT id FROM member WHERE nickname = 'a1'));

-- badge 테이블 데이터 삽입
INSERT INTO badge (name, description, status, acquire_hint, image_url,
                   sort_order, created_at, created_by, updated_at, updated_by)
VALUES ( '뱃지1', '뱃지1 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지1 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 1, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지2', '뱃지2 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지2 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 2, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지3', '뱃지3 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지3 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 3, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지4', '뱃지4 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지4 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 4, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지5', '뱃지5 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지5 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 5, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지6', '뱃지6 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지6 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 6, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지7', '뱃지7 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지7 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 7, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지8', '뱃지8 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지8 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 8, NOW(), 'admin', NOW(), 'admin'),
       ( '뱃지9', '뱃지9 설명설명입니다. 뱃지는 설명이 필요하죠', 'ACTIVE', '뱃지9 획득 힌트 입니다. 뱃지2를 획득하려면 힌트가 필요하죠'
       , 'https://contents.sixshop.com/uploadedFiles/84218/default/image_1547035192141.jpg'
       , 9, NOW(), 'admin', NOW(), 'admin');

-- member_badge 테이블 데이터 삽입
INSERT INTO member_badge (badge_id, main_yn, member_id, created_at, updated_at, created_by, updated_by)
VALUES ((SELECT id FROM badge WHERE name = '뱃지1'), 'Y', (SELECT id FROM member WHERE nickname = 'a1'), NOW(), NOW(),
        'admin', 'admin'),
       ((SELECT id FROM badge WHERE name = '뱃지2'), 'N', (SELECT id FROM member WHERE nickname = 'a1'), NOW(), NOW(),
        'admin', 'admin'),
       ((SELECT id FROM badge WHERE name = '뱃지4'), 'N', (SELECT id FROM member WHERE nickname = 'a1'), NOW(), NOW(),
        'admin', 'admin'),
       ((SELECT id FROM badge WHERE name = '뱃지2'), 'N', (SELECT id FROM member WHERE nickname = 'a2'), NOW(), NOW(),
        'admin', 'admin'),
       ((SELECT id FROM badge WHERE name = '뱃지5'), 'N', (SELECT id FROM member WHERE nickname = 'a2'), NOW(), NOW(),
        'admin', 'admin'),
       ((SELECT id FROM badge WHERE name = '뱃지9'), 'N', (SELECT id FROM member WHERE nickname = 'a2'), NOW(), NOW(),
        'admin', 'admin');



INSERT INTO member_social_account (member_id, provider, created_at, updated_at, created_by, updated_by)
VALUES
    -- a1: KAKAO 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XA', 'KAKAO', NOW(), NOW(), 'admin', 'admin'),

    -- a2: GOOGLE 로그인  
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XB', 'GOOGLE', NOW(), NOW(), 'admin', 'admin'),

    -- a3: APPLE 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XC', 'APPLE', NOW(), NOW(), 'admin', 'admin'),

    -- a4: KAKAO 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XD', 'KAKAO', NOW(), NOW(), 'admin', 'admin'),

    -- a5: GOOGLE 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XE', 'GOOGLE', NOW(), NOW(), 'admin', 'admin'),

    -- a6: APPLE 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XF', 'APPLE', NOW(), NOW(), 'admin', 'admin'),

    -- a7: KAKAO 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XG', 'KAKAO', NOW(), NOW(), 'admin', 'admin'),

    -- a8: GOOGLE 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XH', 'GOOGLE', NOW(), NOW(), 'admin', 'admin'),

    -- a9: APPLE 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XI', 'APPLE', NOW(), NOW(), 'admin', 'admin'),

    -- a10: KAKAO 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XJ', 'KAKAO', NOW(), NOW(), 'admin', 'admin'),

    -- a11: GOOGLE 로그인
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XK', 'GOOGLE', NOW(), NOW(), 'admin', 'admin'),

    -- a12: KAKAO + GOOGLE 복수 계정 (첫 번째)
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XL', 'KAKAO', NOW(), NOW(), 'admin', 'admin'),
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XL', 'GOOGLE', NOW(), NOW(), 'admin', 'admin');

-- member_term_agree 테이블 데이터 삽입
-- 다양한 약관 동의 패턴으로 테스트 케이스 생성
INSERT INTO member_term_agree (member_id, terms_of_agree, privacy, advertisement, created_at, updated_at, created_by,
                               updated_by)
VALUES
    -- a1: 모든 약관 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XA', 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a2: 필수 약관만 동의 (광고 거부)
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XB', 1, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a3: 모든 약관 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XC', 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a4: 필수 약관만 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XD', 1, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a5: 모든 약관 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XE', 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a6: 필수 약관만 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XF', 1, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a7: 모든 약관 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XG', 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a8: 필수 약관만 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XH', 1, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a9: 모든 약관 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XI', 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a10: 필수 약관만 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XJ', 1, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a11: 모든 약관 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XK', 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a12: 필수 약관만 동의
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XL', 1, 1, 0, NOW(), NOW(), 'admin', 'admin');

-- member_alarm_setting 테이블 데이터 삽입
-- 다양한 알림 설정 패턴으로 테스트 케이스 생성
INSERT INTO member_alarm_setting (member_id, base_alarm_yn, todo_bot_yn, feedback_yn, marketing_yn, created_at,
                                  updated_at, created_by, updated_by)
VALUES
    -- a1: 모든 알림 활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XA', 1, 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a2: 기본 알림만 활성화 (마케팅 비활성화)
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XB', 1, 1, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a3: 모든 알림 활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XC', 1, 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a4: 최소 알림 (기본 + 투두봇만)
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XD', 1, 1, 0, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a5: 모든 알림 활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XE', 1, 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a6: 기본 + 피드백 알림만
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XF', 1, 0, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a7: 모든 알림 활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XG', 1, 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a8: 모든 알림 비활성화 (기본만 유지)
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XH', 1, 0, 0, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a9: 모든 알림 활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XI', 1, 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a10: 투두봇 + 피드백만 활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XJ', 1, 1, 1, 0, NOW(), NOW(), 'admin', 'admin'),

    -- a11: 모든 알림 활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XK', 1, 1, 1, 1, NOW(), NOW(), 'admin', 'admin'),

    -- a12: 마케팅만 비활성화
    ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XL', 1, 1, 1, 0, NOW(), NOW(), 'admin', 'admin'); 