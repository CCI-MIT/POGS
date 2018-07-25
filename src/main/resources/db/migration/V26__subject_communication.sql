CREATE TABLE IF NOT EXISTS `subject_communication` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `allowed` TINYINT NULL DEFAULT 0,
  `from_subject_id` BIGINT NOT NULL,
  `to_subject_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_subject_communication_subject_idx` (`from_subject_id`),
  INDEX `fk_subject_communication_subject1_idx` (`to_subject_id`),
  CONSTRAINT `fk_subject_communication_subject`
    FOREIGN KEY (`from_subject_id`)
    REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_subject_communication_subject1`
    FOREIGN KEY (`to_subject_id`)
    REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;