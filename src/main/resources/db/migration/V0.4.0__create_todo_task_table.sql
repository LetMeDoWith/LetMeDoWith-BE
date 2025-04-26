CREATE TABLE IF NOT EXISTS todo_task
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    member_id            BIGINT                NOT NULL,
    todo_task_routine_id BIGINT                NULL,
    task_category_id     BIGINT                NULL,
    title                VARCHAR(255)          NOT NULL,
    status               VARCHAR(20)           NOT NULL,
    date                 date                  NOT NULL,
    start_time           time                  NULL,
    create_at            datetime              NULL,
    updated_at           datetime              NULL,
    created_by           VARCHAR(255)          NULL,
    updated_by           VARCHAR(255)          NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS todo_task_routine
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    dates      TEXT                  NOT NULL,
    create_at  datetime              NULL,
    updated_at datetime              NULL,
    created_by VARCHAR(255)          NULL,
    updated_by VARCHAR(255)          NULL,
    PRIMARY KEY (id)
);