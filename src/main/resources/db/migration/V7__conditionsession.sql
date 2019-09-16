CREATE TABLE IF NOT EXISTS `condition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `condition_name` VARCHAR(255) NULL,
  `study_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_condition_study1_idx` (`study_id` ASC),
  CONSTRAINT `fk_condition_study1`
    FOREIGN KEY (`study_id`)
    REFERENCES `study` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `session_suffix` VARCHAR(45) NULL,
  `session_start_date` DATETIME NULL,
  `condition_id` BIGINT NOT NULL,
  `status` VARCHAR(1) NULL,
  `waiting_room_time` INT NOT NULL DEFAULT 0,
  `intro_page_enabled` TINYINT NULL,
  `intro_text` LONGTEXT NULL,
  `intro_time` INT NOT NULL DEFAULT 0,
  `display_name_change_page_enabled` VARCHAR(45) NULL,
  `display_name_change_time` INT NOT NULL DEFAULT 0,
  `roster_page_enabled` TINYINT NULL,
  `roster_time` INT NOT NULL DEFAULT 0,
  `done_page_enabled` TINYINT NULL,
  `done_page_text` LONGTEXT NULL,
  `done_page_time` INT NOT NULL DEFAULT 0,
  `done_redirect_url` VARCHAR(400) NULL,
  `could_not_assign_to_team_message` VARCHAR(400) NULL,
  `task_execution_type` CHAR(1) NULL,
  `rounds_enabled` TINYINT NULL,
  `number_of_rounds` TINYINT(5) NULL,
  `communication_type` CHAR(1) NULL,
  `chat_bot_name` VARCHAR(255) NULL,
  `scoreboard_enabled` TINYINT NULL,
  `scoreboard_display_type` CHAR(1) NULL,
  `scoreboard_use_display_names` TINYINT NULL,
  `collaboration_todo_list_enabled` TINYINT NULL,
  `collaboration_feedback_widget_enabled` TINYINT NULL,
  `collaboration_voting_widget_enabled` TINYINT NULL,
  `team_creation_moment` CHAR(1) NULL,
  `team_creation_type` CHAR(1) NULL,
  `team_min_size` INT NULL,
  `team_max_size` INT NULL,
  `team_creation_method` CHAR(1) NULL,
  `team_creation_matrix` LONGTEXT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_session_condition1_idx` (`condition_id` ASC),
  CONSTRAINT `fk_session_condition1`
    FOREIGN KEY (`condition_id`)
    REFERENCES `condition` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
  )
ENGINE = InnoDB;