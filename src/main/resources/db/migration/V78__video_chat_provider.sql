ALTER TABLE `session`
    ADD COLUMN `video_chat_notification_email` VARCHAR(300) NULL AFTER `global_chat_completed_task_id`,
    ADD COLUMN `video_chat_upload_file_name` VARCHAR(300) NULL AFTER `video_chat_notification_email`,
    ADD COLUMN `video_chat_transcript_file_name` VARCHAR(300) NULL AFTER `video_chat_upload_file_name`,
    ADD COLUMN `video_chat_upload_webhook_body` TEXT NULL AFTER `video_chat_transcript_file_name`,
    ADD COLUMN `video_chat_transcript_webhook_body` TEXT NULL AFTER `video_chat_upload_webhook_body`;
