CREATE TABLE IF NOT EXISTS `event_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `event` VARCHAR(100) NULL,
  `data` LONGTEXT NULL,
  `timestamp` DATETIME NULL,
  `completed_task_id` BIGINT NOT NULL,
  `subject_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_event_log_completed_task1_idx` (`completed_task_id` ASC),
  INDEX `fk_event_log_subject1_idx` (`subject_id` ASC),
  CONSTRAINT `fk_event_log_completed_task1`
    FOREIGN KEY (`completed_task_id`)
    REFERENCES `completed_task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_event_log_subject1`
    FOREIGN KEY (`subject_id`)
    REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
