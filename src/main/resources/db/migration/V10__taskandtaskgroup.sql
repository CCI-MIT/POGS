CREATE TABLE IF NOT EXISTS `task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `task_name` VARCHAR(255) NULL,
  `task_plugin_type` VARCHAR(45) NOT NULL,
  `solo_task` TINYINT NULL,
  `interaction_time` INT NOT NULL DEFAULT 0,
  `intro_page_enabled` TINYINT NULL,
  `intro_text` LONGTEXT NULL,
  `intro_time` INT NOT NULL DEFAULT 0,
  `primer_page_enabled` TINYINT NULL,
  `primer_text` LONGTEXT NULL,
  `primer_time` INT NOT NULL DEFAULT 0,
  `interaction_widget_enabled` TINYINT NULL,
  `interaction_text` LONGTEXT NULL,
  `communication_type` CHAR(1) NULL,
  `collaboration_todo_list_enabled` TINYINT NULL,
  `collaboration_feedback_widget_enabled` TINYINT NULL,
  `collaboration_voting_widget_enabled` TINYINT NULL,
  `scoring_type` CHAR(1) NULL,
  `subject_communication_id` BIGINT NULL,
  `chat_script_id` BIGINT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `task_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `task_group_name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `task_group_has_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order` INT NULL,
  `task_id` BIGINT NOT NULL,
  `task_group_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_task_group_has_task_task1_idx` (`task_id` ASC),
  INDEX `fk_task_group_has_task_task_group1_idx` (`task_group_id` ASC),
  CONSTRAINT `fk_task_group_has_task_task1`
    FOREIGN KEY (`task_id`)
    REFERENCES `task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_group_has_task_task_group1`
    FOREIGN KEY (`task_group_id`)
    REFERENCES `task_group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;