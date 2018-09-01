ALTER TABLE `session`
DROP FOREIGN KEY `fk_session_condition1`;
ALTER TABLE `session`
DROP COLUMN `condition_id`,
DROP INDEX `fk_session_condition1_idx` ;
ALTER TABLE `session`
ADD COLUMN `study_id` BIGINT(20) NOT NULL,
ADD CONSTRAINT `fk_study_id` FOREIGN KEY (`study_id`)
REFERENCES `study`(`id`);


