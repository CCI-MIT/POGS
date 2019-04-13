CREATE TABLE IF NOT EXISTS `dictionary` (
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
   `dictionary_name` VARCHAR(255) NULL,
   `has_ground_truth` TINYINT(1) NULL,
   PRIMARY KEY (`id`))
  ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `dictionary_has_entry` (
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
   `entry_type` CHAR(1) NULL,
   `entry_category` VARCHAR(400) NULL,
   `entry_value` LONGTEXT NULL,
   `dictionary_id` BIGINT(20) NOT NULL,
   PRIMARY KEY (`id`),
   INDEX `fk_dictionary_has_entry_dictionary_idx` (`dictionary_id`),
   CONSTRAINT `fk_dictionary_has_entry_dictionary`
     FOREIGN KEY (`dictionary_id`)
       REFERENCES `dictionary` (`id`)
       ON DELETE NO ACTION
       ON UPDATE NO ACTION)
  ENGINE = InnoDB;