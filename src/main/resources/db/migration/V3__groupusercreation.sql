CREATE TABLE IF NOT EXISTS `research_group_has_auth_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `research_group_id` BIGINT NOT NULL,
  `auth_user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_research_group_has_auth_user_research_group1_idx` (`research_group_id` ASC),
  INDEX `fk_research_group_has_auth_user_auth_user1_idx` (`auth_user_id` ASC),
  CONSTRAINT `fk_research_group_has_auth_user_research_group1`
    FOREIGN KEY (`research_group_id`)
    REFERENCES `research_group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_research_group_has_auth_user_auth_user1`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;