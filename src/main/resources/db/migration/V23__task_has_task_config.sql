CREATE TABLE IF NOT EXISTS `task_has_task_configuration` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `task_id` BIGINT NOT NULL,
  `task_configuration_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_task_has_task_configuration_task1_idx` (`task_id`),
  INDEX `fk_task_has_task_configuration_task_configuration1_idx` (`task_configuration_id`),
  CONSTRAINT `fk_task_has_task_configuration_task1`
  FOREIGN KEY (`task_id`)
  REFERENCES `task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_has_task_configuration_task_configuration1`
  FOREIGN KEY (`task_configuration_id`)
  REFERENCES `task_configuration` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `completed_task_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_name` VARCHAR(300) NULL,
  `string_value` LONGTEXT NULL,
  `double_value` DOUBLE NULL,
  `integer_value` BIGINT(20) NULL,
  `completed_task_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_completed_task_attribute_completed_task1_idx` (`completed_task_id`),
  CONSTRAINT `fk_completed_task_attribute_completed_task1`
  FOREIGN KEY (`completed_task_id`)
  REFERENCES `completed_task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;