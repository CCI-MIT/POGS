ALTER TABLE `pogs`.`session`
    ADD COLUMN `landing_page_override_enabled` TINYINT(1) NULL DEFAULT 0 AFTER `waiting_room_message`,
    ADD COLUMN `landing_page_override_content` TEXT NULL AFTER `landing_page_override_enabled`;
