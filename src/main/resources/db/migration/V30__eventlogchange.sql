ALTER TABLE `event_log`
DROP FOREIGN KEY `fk_event_log_subject1`;
ALTER TABLE `event_log`
CHANGE COLUMN `event` `event_type` VARCHAR(100) NULL DEFAULT NULL ,
CHANGE COLUMN `data` `event_content` LONGTEXT NULL DEFAULT NULL ,
CHANGE COLUMN `subject_id` `sender_subject_id` BIGINT(20) NOT NULL ,
ADD COLUMN `receiver_subject_id` BIGINT(20) NULL AFTER `sender_subject_id`,
ADD COLUMN `session_id` BIGINT(20) NULL AFTER `receiver_subject_id`;
ALTER TABLE `event_log`
ADD CONSTRAINT `fk_event_log_subject1`
  FOREIGN KEY (`sender_subject_id`)
  REFERENCES `subject` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
ALTER TABLE `event_log`
  ADD COLUMN `sender` VARCHAR(255) NULL AFTER `session_id`,
  ADD COLUMN `receiver` VARCHAR(45) NULL AFTER `sender`;
