CREATE TABLE IF NOT EXISTS `file_entry` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NULL,
  `file_entry_extension` VARCHAR(10) NULL,
  `file_entry_name` VARCHAR(255) NULL,
  `file_size` INT(11) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;