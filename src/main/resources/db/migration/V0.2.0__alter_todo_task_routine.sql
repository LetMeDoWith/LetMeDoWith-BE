-- V0.2.0__alter_todo_task_routine.sql
-- todo_task_routine 테이블에 cycle, pattern, is_exclude_holidays 컬럼 추가

ALTER TABLE todo_task_routine
    ADD COLUMN cycle               VARCHAR(20)    NOT NULL DEFAULT 'NONE',
    ADD COLUMN pattern             VARBINARY(255) NULL,
    ADD COLUMN is_exclude_holidays BOOLEAN        NOT NULL DEFAULT FALSE;