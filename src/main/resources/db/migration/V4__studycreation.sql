CREATE TABLE IF NOT EXISTS `study` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `study_name` VARCHAR(255) NULL,
  `study_description` VARCHAR(45) NULL,
  `study_session_prefix` VARCHAR(100) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `study_has_research_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `study_id` BIGINT NOT NULL,
  `research_group_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_study_has_research_group_study1_idx` (`study_id` ASC),
  INDEX `fk_study_has_research_group_research_group1_idx` (`research_group_id` ASC),
  CONSTRAINT `fk_study_has_research_group_study1`
    FOREIGN KEY (`study_id`)
    REFERENCES `study` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_study_has_research_group_research_group1`
    FOREIGN KEY (`research_group_id`)
    REFERENCES `research_group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;