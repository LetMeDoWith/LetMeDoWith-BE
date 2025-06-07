CREATE TABLE badge
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    create_at    datetime              NULL,
    updated_at   datetime              NULL,
    created_by   VARCHAR(255)          NULL,
    updated_by   VARCHAR(255)          NULL,
    status       VARCHAR(255)          NOT NULL,
    name         VARCHAR(255)          NOT NULL,
    description  VARCHAR(255)          NULL,
    acquire_hint VARCHAR(255)          NULL,
    image_url    VARCHAR(255)          NULL,
    sort_order   INT                   NULL,
    CONSTRAINT pk_badge PRIMARY KEY (id)
);

CREATE TABLE dev_refresh_token
(
    token        VARCHAR(512) NOT NULL,
    access_token VARCHAR(800) NULL,
    member_id    VARCHAR(255) NULL,
    user_agent   VARCHAR(255) NULL,
    expire_at    datetime     NULL,
    CONSTRAINT pk_dev_refresh_token PRIMARY KEY (token)
);

CREATE TABLE dowith_task
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    create_at              datetime              NULL,
    updated_at             datetime              NULL,
    created_by             VARCHAR(255)          NULL,
    updated_by             VARCHAR(255)          NULL,
    member_id              VARCHAR(26)           NOT NULL,
    task_category_id       BIGINT                NULL,
    title                  VARCHAR(255)          NOT NULL,
    status                 VARCHAR(255)          NOT NULL,
    date                   date                  NOT NULL,
    start_time             time                  NULL,
    success_at             datetime              NULL,
    complete_at            datetime              NULL,
    dowith_task_routine_id BIGINT                NULL,
    CONSTRAINT pk_dowith_task PRIMARY KEY (id)
);

CREATE TABLE dowith_task_confirm
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    create_at      datetime              NULL,
    updated_at     datetime              NULL,
    created_by     VARCHAR(255)          NULL,
    updated_by     VARCHAR(255)          NULL,
    dowith_task_id BIGINT                NOT NULL,
    image_url      VARCHAR(255)          NULL,
    CONSTRAINT pk_dowith_task_confirm PRIMARY KEY (id)
);

CREATE TABLE dowith_task_routine
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    create_at  datetime              NULL,
    updated_at datetime              NULL,
    created_by VARCHAR(255)          NULL,
    updated_by VARCHAR(255)          NULL,
    dates      text                  NULL,
    CONSTRAINT pk_dowith_task_routine PRIMARY KEY (id)
);

CREATE TABLE holiday
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    create_at    datetime              NULL,
    updated_at   datetime              NULL,
    created_by   VARCHAR(255)          NULL,
    updated_by   VARCHAR(255)          NULL,
    country_code VARCHAR(255)          NOT NULL,
    date         date                  NOT NULL,
    name         VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_holiday PRIMARY KEY (id)
);

CREATE TABLE member
(
    id                  VARCHAR(26)  NOT NULL,
    create_at           datetime     NULL,
    updated_at          datetime     NULL,
    created_by          VARCHAR(255) NULL,
    updated_by          VARCHAR(255) NULL,
    subject             VARCHAR(255) NULL,
    status              VARCHAR(255) NOT NULL,
    task_complete_level VARCHAR(255) NULL,
    nickname            VARCHAR(255) NULL,
    self_description    VARCHAR(255) NULL,
    gender              VARCHAR(2)   NULL,
    date_of_birth       date         NULL,
    type                VARCHAR(255) NOT NULL,
    profile_image_url   VARCHAR(255) NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

CREATE TABLE member_alarm_setting
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    create_at     datetime              NULL,
    updated_at    datetime              NULL,
    created_by    VARCHAR(255)          NULL,
    updated_by    VARCHAR(255)          NULL,
    member_id     VARCHAR(26)           NOT NULL,
    base_alarm_yn BIT(1)                NOT NULL,
    todo_bot_yn   BIT(1)                NOT NULL,
    feedback_yn   BIT(1)                NOT NULL,
    marketing_yn  BIT(1)                NOT NULL,
    CONSTRAINT pk_member_alarm_setting PRIMARY KEY (id)
);

CREATE TABLE member_badge
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    create_at  datetime              NULL,
    updated_at datetime              NULL,
    created_by VARCHAR(255)          NULL,
    updated_by VARCHAR(255)          NULL,
    member_id  VARCHAR(26)           NULL,
    badge_id   BIGINT                NOT NULL,
    main_yn    VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_member_badge PRIMARY KEY (id)
);

CREATE TABLE member_follow
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    create_at    datetime              NULL,
    updated_at   datetime              NULL,
    created_by   VARCHAR(255)          NULL,
    updated_by   VARCHAR(255)          NULL,
    follower_id  VARCHAR(26)           NOT NULL,
    following_id VARCHAR(26)           NOT NULL,
    CONSTRAINT pk_member_follow PRIMARY KEY (id)
);

CREATE TABLE member_social_account
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    create_at  datetime              NULL,
    updated_at datetime              NULL,
    created_by VARCHAR(255)          NULL,
    updated_by VARCHAR(255)          NULL,
    member_id  VARCHAR(26)           NOT NULL,
    provider   VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_member_social_account PRIMARY KEY (id)
);

CREATE TABLE member_status_history
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    create_at         datetime              NULL,
    updated_at        datetime              NULL,
    created_by        VARCHAR(255)          NULL,
    updated_by        VARCHAR(255)          NULL,
    member_id         VARCHAR(26)           NOT NULL,
    status            VARCHAR(255)          NOT NULL,
    status_changed_at datetime              NOT NULL,
    status_end_at     datetime              NOT NULL,
    CONSTRAINT pk_member_status_history PRIMARY KEY (id)
);

CREATE TABLE member_term_agree
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    create_at      datetime              NULL,
    updated_at     datetime              NULL,
    created_by     VARCHAR(255)          NULL,
    updated_by     VARCHAR(255)          NULL,
    member_id      VARCHAR(26)           NOT NULL,
    terms_of_agree BIT(1)                NOT NULL,
    privacy        BIT(1)                NOT NULL,
    advertisement  BIT(1)                NOT NULL,
    CONSTRAINT pk_member_term_agree PRIMARY KEY (id)
);

CREATE TABLE task_category
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    create_at          datetime              NULL,
    updated_at         datetime              NULL,
    created_by         VARCHAR(255)          NULL,
    updated_by         VARCHAR(255)          NULL,
    title              VARCHAR(255)          NOT NULL,
    active_yn          VARCHAR(255)          NOT NULL,
    creation_type      VARCHAR(255)          NOT NULL,
    emoji              VARCHAR(255)          NOT NULL,
    category_holder_id VARCHAR(26)           NULL,
    CONSTRAINT pk_task_category PRIMARY KEY (id)
);

CREATE TABLE todo_task
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    create_at            datetime              NULL,
    updated_at           datetime              NULL,
    created_by           VARCHAR(255)          NULL,
    updated_by           VARCHAR(255)          NULL,
    member_id            VARCHAR(26)           NOT NULL,
    task_category_id     BIGINT                NULL,
    title                VARCHAR(255)          NOT NULL,
    status               VARCHAR(255)          NOT NULL,
    date                 date                  NOT NULL,
    start_time           time                  NULL,
    todo_task_routine_id BIGINT                NULL,
    CONSTRAINT pk_todo_task PRIMARY KEY (id)
);

CREATE TABLE todo_task_routine
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    create_at  datetime              NULL,
    updated_at datetime              NULL,
    created_by VARCHAR(255)          NULL,
    updated_by VARCHAR(255)          NULL,
    dates      text                  NULL,
    CONSTRAINT pk_todo_task_routine PRIMARY KEY (id)
);

CREATE TABLE task_summary
(
    id                                    BIGINT AUTO_INCREMENT NOT NULL,
    member_id                             VARCHAR(26)           NOT NULL,
    remained_dowith_task_count            INT                   NOT NULL DEFAULT 0,
    remained_dowith_task_count_updated_at datetime              NULL,
    last_attendance_date                  date                  NULL,
    task_complete_level                   VARCHAR(10)           NOT NULL DEFAULT 'GOOD',
    create_at                             datetime              NULL,
    updated_at                            datetime              NULL,
    created_by                            VARCHAR(255)          NULL,
    updated_by                            VARCHAR(255)          NULL,
    CONSTRAINT pk_task_summary PRIMARY KEY (id)
);

ALTER TABLE dowith_task_confirm
    ADD CONSTRAINT uc_dowith_task_confirm_dowith_task UNIQUE (dowith_task_id);

ALTER TABLE member_alarm_setting
    ADD CONSTRAINT uc_member_alarm_setting_member UNIQUE (member_id);

ALTER TABLE member_term_agree
    ADD CONSTRAINT uc_member_term_agree_member UNIQUE (member_id);

ALTER TABLE dowith_task_confirm
    ADD CONSTRAINT FK_DOWITH_TASK_CONFIRM_ON_DOWITH_TASK FOREIGN KEY (dowith_task_id) REFERENCES dowith_task (id);

ALTER TABLE dowith_task
    ADD CONSTRAINT FK_DOWITH_TASK_ON_DOWITH_TASK_ROUTINE FOREIGN KEY (dowith_task_routine_id) REFERENCES dowith_task_routine (id);

ALTER TABLE member_alarm_setting
    ADD CONSTRAINT FK_MEMBER_ALARM_SETTING_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE member_badge
    ADD CONSTRAINT FK_MEMBER_BADGE_ON_BADGE FOREIGN KEY (badge_id) REFERENCES badge (id);

ALTER TABLE todo_task
    ADD CONSTRAINT FK_TODO_TASK_ON_TODO_TASK_ROUTINE FOREIGN KEY (todo_task_routine_id) REFERENCES todo_task_routine (id);

ALTER TABLE member_follow
    ADD CONSTRAINT FK_MEMBER_FOLLOW_ON_FOLLOWER FOREIGN KEY (follower_id) REFERENCES member (id);

ALTER TABLE member_follow
    ADD CONSTRAINT FK_MEMBER_FOLLOW_ON_FOLLOWING FOREIGN KEY (following_id) REFERENCES member (id);

ALTER TABLE member_social_account
    ADD CONSTRAINT FK_MEMBER_SOCIAL_ACCOUNT_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE member_status_history
    ADD CONSTRAINT FK_MEMBER_STATUS_HISTORY_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE member_term_agree
    ADD CONSTRAINT FK_MEMBER_TERM_AGREE_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE task_summary
    ADD CONSTRAINT FK_TASK_SUMMARY_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);