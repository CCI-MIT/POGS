CREATE TABLE IF NOT EXISTS `todo_entry` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `text` VARCHAR(400) NULL,
  `todo_entry_date` DATETIME NULL,
  `marked_done_date` DATETIME NULL,
  `marked_done` TINYINT NULL,
  `completed_task_id` BIGINT NOT NULL,
  `creator_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `todo_entry_assignment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `assignment_date` DATETIME NULL,
  `current_assigned` TINYINT NULL,
  `todo_entry_id` BIGINT NOT NULL,
  `subject_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_todo_entry_assignment_todo_entry1_idx` (`todo_entry_id` ASC),

  CONSTRAINT `fk_todo_entry_assignment_todo_entry1`
    FOREIGN KEY (`todo_entry_id`)
    REFERENCES `todo_entry` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `voting_pool` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `voting_question` VARCHAR(255) NULL,
  `completed_task_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `voting_pool_option` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `voting_option` VARCHAR(255) NULL,
  `voting_pool_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_voting_pool_option_voting_pool1_idx` (`voting_pool_id` ASC),
  CONSTRAINT `fk_voting_pool_option_voting_pool1`
    FOREIGN KEY (`voting_pool_id`)
    REFERENCES `voting_pool` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `voting_pool_vote` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `voting_pool_option_id` BIGINT NOT NULL,
  `subject_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_voting_pool_vote_voting_pool_option1_idx` (`voting_pool_option_id` ASC),
  CONSTRAINT `fk_voting_pool_vote_voting_pool_option1`
    FOREIGN KEY (`voting_pool_option_id`)
    REFERENCES `voting_pool_option` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;