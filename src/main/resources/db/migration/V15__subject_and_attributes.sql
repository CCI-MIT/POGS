CREATE TABLE IF NOT EXISTS `subject` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `subject_external_id` VARCHAR(255) NULL,
  `subject_display_name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `subject_attribute` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `attribute_name` VARCHAR(300) NULL COMMENT '	',
  `string_value` LONGTEXT NULL,
  `integer_value` BIGINT(20) NULL,
  `real_value` DOUBLE NULL,
  `subject_id` BIGINT NOT NULL,
  `latest` TINYINT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_subject_attribute_subject1_idx` (`subject_id`),
  CONSTRAINT `fk_subject_attribute_subject1`
  FOREIGN KEY (`subject_id`)
  REFERENCES `subject` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;