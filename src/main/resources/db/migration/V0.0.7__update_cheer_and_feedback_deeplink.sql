UPDATE notification_template
SET app_deep_link = 'letmedowith://cheer-collection?dowithTaskId={{dowithTaskId}}'
WHERE code = 'LIKE_RECEIVED';

UPDATE notification_template
SET app_deep_link = 'letmedowith://received-feedback?dowithTaskId={{dowithTaskId}}'
WHERE code IN ('FEEDBACK_RECEIVED_1', 'FEEDBACK_RECEIVED_2', 'FEEDBACK_RECEIVED_3', 'FEEDBACK_RECEIVED_4');
