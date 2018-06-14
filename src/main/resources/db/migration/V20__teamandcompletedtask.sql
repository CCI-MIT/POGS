CREATE TABLE IF NOT EXISTS `team` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL,
  `round_id` BIGINT NULL,
  `task_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_team_session1_idx` (`session_id`),
  INDEX `fk_team_round1_idx` (`round_id`),
  INDEX `fk_team_task1_idx` (`task_id`),
  CONSTRAINT `fk_team_session1`
    FOREIGN KEY (`session_id`)
    REFERENCES `session` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_round1`
    FOREIGN KEY (`round_id`)
    REFERENCES `round` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_task1`
    FOREIGN KEY (`task_id`)
    REFERENCES `task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `completed_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `task_final_snapshot` LONGTEXT NULL,
  `round_id` BIGINT NOT NULL,
  `task_id` BIGINT NOT NULL,
  `subject_id` BIGINT NULL,
  `team_id` BIGINT NULL,
  `start_time` DATETIME NULL,
  `compled_task_order` SMALLINT(5) NULL,
  `expected_start_time` DATETIME NULL,
  `expected_finish_time` DATETIME NULL,
  `inferred_end_time` VARCHAR(45) NULL,
  `solo` VARCHAR(45) NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_completed_task_round1_idx` (`round_id` ASC),
  INDEX `fk_completed_task_task1_idx` (`task_id` ASC),
  INDEX `fk_completed_task_team1_idx` (`team_id` ASC),
  INDEX `fk_completed_task_subject1_idx` (`subject_id` ASC),
  CONSTRAINT `fk_completed_task_round1`
    FOREIGN KEY (`round_id`)
    REFERENCES `round` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_completed_task_task1`
    FOREIGN KEY (`task_id`)
    REFERENCES `task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_completed_task_team1`
    FOREIGN KEY (`team_id`)
    REFERENCES `team` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_completed_task_subject1`
    FOREIGN KEY (`subject_id`)
    REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;