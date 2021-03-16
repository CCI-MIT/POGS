ALTER TABLE `session`
    ADD COLUMN `global_chat_completed_task_id` BIGINT NULL DEFAULT NULL AFTER `record_session_save_ephemeral_events`;
