ALTER TABLE `pogs`.`session`
    ADD COLUMN `video_chat_recording_enabled` TINYINT(1) NULL DEFAULT 0 AFTER `video_chat_transcript_webhook_body`;
