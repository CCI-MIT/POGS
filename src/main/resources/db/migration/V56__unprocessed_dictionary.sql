CREATE TABLE IF NOT EXISTS `unprocessed_dictionary_entry` (
     `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
     `entry_predicted_category` VARCHAR(255) NULL,
     `entry_value` LONGTEXT NULL,
     `dictionary_id` BIGINT(20) NULL DEFAULT NULL,
     `has_been_processed` TINYINT(1) NULL DEFAULT 0,
     PRIMARY KEY (`id`))
  ENGINE = InnoDB;