CREATE TABLE notification
(
    id                         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id                  VARCHAR(26)  NOT NULL,
    title                      TEXT         NOT NULL,
    body                       TEXT         NOT NULL,
    deep_link                  TEXT,
    confirmed_yn               VARCHAR(1)   NOT NULL, -- ENUM 대체
    confirm_date_time          DATETIME,
    notification_template_code VARCHAR(50),
    create_at                  timestamp    NULL,
    updated_at                 timestamp    NULL,
    created_by                 VARCHAR(255) NULL,
    updated_by                 VARCHAR(255) NULL
);

CREATE TABLE notification_token
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  VARCHAR(26)  NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expired_yn VARCHAR(1)   NOT NULL, -- ENUM 대체
    create_at  timestamp    NULL,
    updated_at timestamp    NULL,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL
);

CREATE TABLE notification_template
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL UNIQUE,
    title         TEXT         NOT NULL,
    body          TEXT         NOT NULL,
    app_deep_link TEXT,
    create_at     timestamp    NULL,
    updated_at    timestamp    NULL,
    created_by    VARCHAR(255) NULL,
    updated_by    VARCHAR(255) NULL
);

INSERT INTO notification_template (code,
                                   title,
                                   body,
                                   app_deep_link,
                                   create_at,
                                   updated_at,
                                   created_by,
                                   updated_by)
VALUES ('SIGN_UP_COMPLETE',
        '👋 환영해요 {{userName}}님! 회원가입이 완료되었습니다.',
        '프로필을 설정하고 서비스를 시작해 보세요!',
        NULL,
        NOW(),
        NOW(),
        'system',
        'system'),
       ('ENABLE_NOTIFICATION_REQUEST',
        '🔔알림을 켜고 더 재미있게 사용해봐요!',
        '알림이 있어야 두윗러로써의 삶을 제대로 경험할 수 있답니다.\n이제 알림을 켜고 시작해볼까요?',
        NULL,
        NOW(),
        NOW(),
        'system',
        'system');
