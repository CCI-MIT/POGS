ALTER TABLE `session`
    ADD COLUMN `before_session_script_id` BIGINT(20) NULL DEFAULT NULL AFTER `display_name_generation_type`,
    ADD COLUMN `after_session_script_id` VARCHAR(45) NULL DEFAULT NULL AFTER `before_session_script_id`,
    ADD COLUMN `perpetual_session_timeout_limit` INT(11) NULL AFTER `after_session_script_id`,
    ADD COLUMN `perpetual_session_timeout_message` VARCHAR(255) NULL AFTER `perpetual_session_timeout_limit`,
    ADD COLUMN `dispatcher_session` TINYINT(1) NULL DEFAULT 0 AFTER `perpetual_session_timeout_message`;
