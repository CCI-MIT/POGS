CREATE TABLE IF NOT EXISTS `session_has_task_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT NOT NULL,
  `task_group_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_session_has_task_group_session1_idx` (`session_id` ASC),
  INDEX `fk_session_has_task_group_task_group1_idx` (`task_group_id` ASC),
  CONSTRAINT `fk_session_has_task_group_session1`
    FOREIGN KEY (`session_id`)
    REFERENCES `session` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_session_has_task_group_task_group1`
    FOREIGN KEY (`task_group_id`)
    REFERENCES `task_group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;