CREATE TABLE letmedowith_app.task_category
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    create_at          datetime              NULL,
    updated_at         datetime              NULL,
    created_by         VARCHAR(255)          NULL,
    updated_by         VARCHAR(255)          NULL,
    title              VARCHAR(255)          NOT NULL,
    creation_type      VARCHAR(255)          NOT NULL,
    emoji              VARCHAR(255)          NOT NULL,
    category_holder_id BIGINT                NULL,
    CONSTRAINT pk_taskcategory PRIMARY KEY (id)
);