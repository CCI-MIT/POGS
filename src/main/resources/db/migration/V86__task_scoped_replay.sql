ALTER TABLE `pogs`.`task`
    ADD COLUMN `replay_from_session_id` BIGINT NULL AFTER `score_page_enabled`;
