UPDATE notification_template
SET app_deep_link = 'letmedowith://home?date={{date}}'
WHERE code IN ('FEEDBACK_RECEIVED_1', 'FEEDBACK_RECEIVED_2', 'FEEDBACK_RECEIVED_3', 'FEEDBACK_RECEIVED_4');
