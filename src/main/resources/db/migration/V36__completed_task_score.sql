CREATE TABLE IF NOT EXISTS `completed_task_score` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `total_score` FLOAT NULL,
  `completed_task_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_completed_task_score_completed_task1_idx` (`completed_task_id`),
  CONSTRAINT `fk_completed_task_score_completed_task1`
    FOREIGN KEY (`completed_task_id`)
    REFERENCES `completed_task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;