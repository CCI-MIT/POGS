ALTER TABLE `session`
    ADD COLUMN `display_name_generation_type` CHAR(1) NULL DEFAULT NULL
        AFTER `display_name_generation_enabled`;
