-- V0.1.1__insert_initial_data.sql
-- Flyway 마이그레이션: 초기 데이터 삽입 (String Member ID 체계 적용)
-- 주의: 이 스크립트는 Member ID가 자동 생성(TSID)되는 새 스키마에 맞춰 초기 데이터를 다시 삽입합니다.
-- Member ID 참조 시에는 주로 nickname을 사용하여 조회합니다.

-- member 테이블 데이터 삽입
INSERT INTO member (id, create_at, updated_at, created_by, updated_by, subject, status,
                    nickname, self_description,
                    type, profile_image_url)
VALUES ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XA', NOW(), NOW(), 'admin', 'admin', '1810473492', '3', 'a1', 'a1 자기소개',
        'USER',
        's3:://a1_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XB', NOW(), NOW(), 'admin', 'admin', '2810473493', '1', 'a2', 'a2 자기소개',
        'USER',
        's3:://a2_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XC', NOW(), NOW(), 'admin', 'admin', '3810473494', '3', 'a3', 'a3 자기소개',
        'USER',
        's3:://a3_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XD', NOW(), NOW(), 'admin', 'admin', '4810473495', '3', 'a4', 'a4 자기소개',
        'USER',
        's3:://a4_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XE', NOW(), NOW(), 'admin', 'admin', '5810473496', '3', 'a5', 'a5 자기소개',
        'USER',
        's3:://a5_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XF', NOW(), NOW(), 'admin', 'admin', '6810473497', '3', 'a6', 'a6 자기소개',
        'USER',
        's3:://a6_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XG', NOW(), NOW(), 'admin', 'admin', '7810473498', '3', 'a7', 'a7 자기소개',
        'USER',
        's3:://a7_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XH', NOW(), NOW(), 'admin', 'admin', '8810473499', '3', 'a8', 'a8 자기소개',
        'USER',
        's3:://a8_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XI', NOW(), NOW(), 'admin', 'admin', '9810473490', '3', 'a9', 'a9 자기소개',
        'USER',
        's3:://a9_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XJ', NOW(), NOW(), 'admin', 'admin', '9110473491', '3', 'a10', 'a10 자기소개',
        'USER',
        's3:://a10_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XK', NOW(), NOW(), 'admin', 'admin', '9210473492', '3', 'a11', 'a11 자기소개',
        'USER',
        's3:://a11_profile_image'),
       ('01HXQ2X7Z7Q6XJX4X2X7Z7Q6XL', NOW(), NOW(), 'admin', 'admin', '9310473493', '3', 'a12', 'a12 자기소개',
        'USER',
        's3:://a12_profile_image');

INSERT INTO task_summary (member_id, remained_dowith_task_count, remained_dowith_task_count_updated_at,
                          last_attendance_date, task_complete_level)
VALUES ((SELECT id FROM member WHERE nickname = 'a1'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a2'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a3'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a4'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a5'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a6'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a7'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a8'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a9'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a10'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a11'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a12'), 0, NOW(), NULL, '3');

-- task_category 테이블 데이터 삽입
INSERT INTO task_category (create_at, updated_at, created_by, updated_by, title, active_yn,
                           creation_type, emoji, category_holder_id)
VALUES (NOW(), NOW(), 'admin', 'admin', '약속', 'Y', 'COMMON', '🙋‍♀️', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '시험', 'Y', 'COMMON', '🗓️', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '운동', 'Y', 'COMMON', '🦺', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '일상', 'Y', 'COMMON', '🏙️', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '공부', 'Y', 'COMMON', '📝', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '독서', 'Y', 'COMMON', '📘', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '작업', 'Y', 'COMMON', '👩‍💻', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '기타', 'Y', 'COMMON', '⏰', 'SYSTEM');

-- member_follow 테이블 데이터 삽입
INSERT INTO member_follow (create_at, updated_at, created_by, updated_by, follower_id, following_id)
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

INSERT INTO member_follow (create_at, updated_at, created_by, updated_by, follower_id, following_id)
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
                   sort_order, create_at, created_by, updated_at, updated_by)
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
INSERT INTO member_badge (badge_id, main_yn, member_id, create_at, updated_at, created_by, updated_by)
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