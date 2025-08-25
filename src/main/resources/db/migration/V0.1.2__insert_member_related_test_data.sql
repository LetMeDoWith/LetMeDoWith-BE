-- V0.1.2__insert_member_related_test_data.sql
-- Flyway 마이그레이션: 멤버 유관 테이블 테스트 데이터 삽입
-- member_social_account, member_term_agree, member_alarm_setting 테이블에 테스트 데이터 추가

-- member_social_account 테이블 데이터 삽입
-- 멤버들에게 다양한 소셜 로그인 계정 할당
INSERT INTO member_social_account (member_id, provider, create_at, updated_at, created_by, updated_by)
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
INSERT INTO member_term_agree (member_id, terms_of_agree, privacy, advertisement, create_at, updated_at, created_by, updated_by)
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
INSERT INTO member_alarm_setting (member_id, base_alarm_yn, todo_bot_yn, feedback_yn, marketing_yn, create_at, updated_at, created_by, updated_by)
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