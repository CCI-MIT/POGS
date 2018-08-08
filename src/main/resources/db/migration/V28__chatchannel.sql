CREATE TABLE IF NOT EXISTS `chat_channel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `channel_name` VARCHAR(255) NULL,
  `creator_subject_id` BIGINT NULL,
  `completed_task_id` BIGINT NULL,
  `session_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_communication_channel_subject1_idx` (`creator_subject_id`),
  INDEX `fk_communication_channel_completed_task1_idx` (`completed_task_id`),
  INDEX `fk_chat_channel_session1_idx` (`session_id`),
  CONSTRAINT `fk_communication_channel_subject1`
    FOREIGN KEY (`creator_subject_id`)
    REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_communication_channel_completed_task1`
    FOREIGN KEY (`completed_task_id`)
    REFERENCES `completed_task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_chat_channel_session1`
    FOREIGN KEY (`session_id`)
    REFERENCES `session` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `subject_has_channel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NULL,
  `subject_id` BIGINT NOT NULL,
  `added_by_subject_id` BIGINT NULL,
  `chat_channel_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_subject_has_channel_subject1_idx` (`subject_id`),
  INDEX `fk_subject_has_channel_subject2_idx` (`added_by_subject_id`),
  INDEX `fk_subject_has_channel_communication_channel1_idx` (`chat_channel_id`),
  CONSTRAINT `fk_subject_has_channel_subject1`
  FOREIGN KEY (`subject_id`)
  REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_subject_has_channel_subject2`
  FOREIGN KEY (`added_by_subject_id`)
  REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_subject_has_channel_communication_channel1`
  FOREIGN KEY (`chat_channel_id`)
  REFERENCES `chat_channel` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;