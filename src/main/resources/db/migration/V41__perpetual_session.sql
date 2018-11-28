ALTER TABLE `session`
ADD COLUMN `session_start_type` CHAR(1) NULL AFTER `study_id`,
ADD COLUMN `perpetual_start_date` DATETIME NULL AFTER `session_start_type`,
ADD COLUMN `perpetual_end_date` DATETIME NULL AFTER `perpetual_start_date`,
ADD COLUMN `perpetual_subjects_number` INT(10) NULL AFTER `perpetual_end_date`,
ADD COLUMN `perpetual_subjects_prefix` VARCHAR(255) NULL AFTER `perpetual_subjects_number`;
