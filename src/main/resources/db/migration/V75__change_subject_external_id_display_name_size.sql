ALTER TABLE `subject`
    CHANGE COLUMN `subject_external_id` `subject_external_id` VARCHAR(555) NULL DEFAULT NULL ,
    CHANGE COLUMN `subject_display_name` `subject_display_name` VARCHAR(555) NULL DEFAULT NULL ;

ALTER TABLE `subject`
    ADD COLUMN `created_at` DATETIME NULL AFTER `pogs_unique_hash`;
