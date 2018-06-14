CREATE TABLE IF NOT EXISTS `team_has_subject` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `team_id` BIGINT NOT NULL,
  `subject_id` BIGINT NOT NULL,
  INDEX `fk_team_has_subject_team1_idx` (`team_id`),
  INDEX `fk_team_has_subject_subject1_idx` (`subject_id`),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_team_has_subject_team1`
    FOREIGN KEY (`team_id`)
    REFERENCES `team` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_has_subject_subject1`
    FOREIGN KEY (`subject_id`)
    REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB