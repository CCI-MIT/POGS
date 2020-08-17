ALTER TABLE `session`
    ADD COLUMN `record_session_save_ephemeral_events` TINYINT(1) NULL DEFAULT 0 AFTER `robot_session_event_script_id`;
