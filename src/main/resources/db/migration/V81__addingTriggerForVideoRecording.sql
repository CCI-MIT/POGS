ALTER TABLE `pogs`.`session`
    ADD COLUMN `trigger_task_for_video_chat` VARCHAR(45) NULL AFTER `video_chat_recording_enabled`;