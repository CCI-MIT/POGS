CREATE TABLE IF NOT EXISTS `individual_subject_score` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `individual_score` FLOAT NULL,
     `extra_data` TEXT NULL,
     `completed_task_id` BIGINT NOT NULL,
     `subject_id` BIGINT NOT NULL,
     PRIMARY KEY (`id`),
     INDEX `fk_subject_score_completed_task1_idx` (`completed_task_id` ASC),
     INDEX `fk_individual_subject_score_subject1_idx` (`subject_id` ASC),
     CONSTRAINT `fk_subject_score_completed_task1`
         FOREIGN KEY (`completed_task_id`)
             REFERENCES `completed_task` (`id`)
             ON DELETE NO ACTION
             ON UPDATE NO ACTION,
     CONSTRAINT `fk_individual_subject_score_subject1`
         FOREIGN KEY (`subject_id`)
             REFERENCES `subject` (`id`)
             ON DELETE NO ACTION
             ON UPDATE NO ACTION)
    ENGINE = InnoDB;