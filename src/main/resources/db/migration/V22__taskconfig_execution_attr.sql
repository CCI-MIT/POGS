CREATE TABLE IF NOT EXISTS `task_configuration` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `configuration_name` VARCHAR(45) NULL,
  `taskPluginName` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `task_execution_attribute` (
  `id` BIGINT NOT NULL,
  `attribute_name` VARCHAR(300) NULL,
  `string_value` LONGTEXT NULL,
  `integer_value` BIGINT(20) NULL,
  `double_value` DOUBLE NULL,
  `task_configuration_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_task_execution_attribute_task_configuration1_idx` (`task_configuration_id`),
  CONSTRAINT `fk_task_execution_attribute_task_configuration1`
    FOREIGN KEY (`task_configuration_id`)
    REFERENCES `task_configuration` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;