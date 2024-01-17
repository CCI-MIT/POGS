ALTER TABLE `pogs`.`task`
    ADD COLUMN `replay_from_session_enabled` TINYINT NULL DEFAULT 0 AFTER `replay_from_session_id`;
