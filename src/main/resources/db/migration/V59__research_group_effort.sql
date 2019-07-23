
CREATE TABLE IF NOT EXISTS `executable_script_has_research_group` (
                                                                    `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
                                                                    `executable_script_id` BIGINT NOT NULL,
                                                                    `research_group_id` BIGINT NOT NULL,
                                                                    PRIMARY KEY (`id`),
                                                                    INDEX `fk_task_group_has_research_group_executable_script_id1_idx` (`executable_script_id` ASC),
                                                                    INDEX `fk_task_group_has_research_group_research_group1_idx` (`research_group_id` ASC),
                                                                    CONSTRAINT `fk_task_group_has_research_group_executable_script_id1`
                                                                      FOREIGN KEY (`executable_script_id`)
                                                                        REFERENCES `executable_script` (`id`)
                                                                        ON DELETE NO ACTION
                                                                        ON UPDATE NO ACTION,
                                                                    CONSTRAINT `fk_task_group_has_research_group_research_group6`
                                                                      FOREIGN KEY (`research_group_id`)
                                                                        REFERENCES `research_group` (`id`)
                                                                        ON DELETE NO ACTION
                                                                        ON UPDATE NO ACTION)
  ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `chat_script_has_research_group` (
                                                              `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
                                                              `chat_script_id` BIGINT NOT NULL,
                                                              `research_group_id` BIGINT NOT NULL,
                                                              PRIMARY KEY (`id`),
                                                              INDEX `fk_task_group_has_research_group_chat_script_id1_idx` (`chat_script_id` ASC),
                                                              INDEX `fk_task_group_has_research_group_research_group1_idx` (`research_group_id` ASC),
                                                              CONSTRAINT `fk_task_group_has_research_group_chat_script_id1`
                                                                FOREIGN KEY (`chat_script_id`)
                                                                  REFERENCES `chat_script` (`id`)
                                                                  ON DELETE NO ACTION
                                                                  ON UPDATE NO ACTION,
                                                              CONSTRAINT `fk_task_group_has_research_group_research_group2`
                                                                FOREIGN KEY (`research_group_id`)
                                                                  REFERENCES `research_group` (`id`)
                                                                  ON DELETE NO ACTION
                                                                  ON UPDATE NO ACTION)
  ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `dictionary_has_research_group` (
                                                             `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
                                                             `dictionary_id` BIGINT NOT NULL,
                                                             `research_group_id` BIGINT NOT NULL,
                                                             PRIMARY KEY (`id`),
                                                             INDEX `fk_task_group_has_research_group_dictionary_id1_idx` (`dictionary_id` ASC),
                                                             INDEX `fk_task_group_has_research_group_research_group1_idx` (`research_group_id` ASC),
                                                             CONSTRAINT `fk_task_group_has_research_group_dictionary_id1`
                                                               FOREIGN KEY (`dictionary_id`)
                                                                 REFERENCES `dictionary` (`id`)
                                                                 ON DELETE NO ACTION
                                                                 ON UPDATE NO ACTION,
                                                             CONSTRAINT `fk_task_group_has_research_group_research_group3`
                                                               FOREIGN KEY (`research_group_id`)
                                                                 REFERENCES `research_group` (`id`)
                                                                 ON DELETE NO ACTION
                                                                 ON UPDATE NO ACTION)
  ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `task_group_has_research_group` (
                                                             `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
                                                             `task_group_id` BIGINT NOT NULL,
                                                             `research_group_id` BIGINT NOT NULL,
                                                             PRIMARY KEY (`id`),
                                                             INDEX `fk_task_group_has_research_group_task_group1_idx` (`task_group_id` ASC),
                                                             INDEX `fk_task_group_has_research_group_research_group1_idx` (`research_group_id` ASC),
                                                             CONSTRAINT `fk_task_group_has_research_group_task_group1`
                                                               FOREIGN KEY (`task_group_id`)
                                                                 REFERENCES `task_group` (`id`)
                                                                 ON DELETE NO ACTION
                                                                 ON UPDATE NO ACTION,
                                                             CONSTRAINT `fk_task_group_has_research_group_research_group1`
                                                               FOREIGN KEY (`research_group_id`)
                                                                 REFERENCES `research_group` (`id`)
                                                                 ON DELETE NO ACTION
                                                                 ON UPDATE NO ACTION)
  ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `task_configuration_has_research_group` (
                                                                     `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
                                                                     `task_configuration_id` BIGINT NOT NULL,
                                                                     `research_group_id` BIGINT NOT NULL,
                                                                     PRIMARY KEY (`id`),
                                                                     INDEX `fk_task_configuration_has_research_group_task_configuration_idx` (`task_configuration_id` ASC),
                                                                     INDEX `fk_task_configuration_has_research_group_research_group1_idx` (`research_group_id` ASC),
                                                                     CONSTRAINT `fk_task_configuration_has_research_group_task_configuration1`
                                                                       FOREIGN KEY (`task_configuration_id`)
                                                                         REFERENCES `task_configuration` (`id`)
                                                                         ON DELETE NO ACTION
                                                                         ON UPDATE NO ACTION,
                                                                     CONSTRAINT `fk_task_configuration_has_research_group_research_group5`
                                                                       FOREIGN KEY (`research_group_id`)
                                                                         REFERENCES `research_group` (`id`)
                                                                         ON DELETE NO ACTION
                                                                         ON UPDATE NO ACTION)
  ENGINE = InnoDB;