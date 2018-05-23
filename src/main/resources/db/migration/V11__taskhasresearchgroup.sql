CREATE TABLE IF NOT EXISTS `task_has_research_group` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `research_group_id` BIGINT NOT NULL,
  `task_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_task_has_research_group_research_group1_idx` (`research_group_id`),
  INDEX `fk_task_has_research_group_task1_idx` (`task_id`),
  CONSTRAINT `fk_task_has_research_group_research_group1`
    FOREIGN KEY (`research_group_id`)
    REFERENCES `research_group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_has_research_group_task1`
    FOREIGN KEY (`task_id`)
    REFERENCES `task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB