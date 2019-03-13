CREATE TABLE IF NOT EXISTS `executable_script` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `script_name` VARCHAR(155) NULL,
    `script_type` CHAR(1) NULL,
    `script_content` TEXT NULL,
    PRIMARY KEY (`id`))
  ENGINE = InnoDB;
