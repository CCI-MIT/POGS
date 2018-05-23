
CREATE TABLE IF NOT EXISTS `chat_script` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `chat_script_name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `chat_entry` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `chat_entry_value` VARCHAR(400) NULL,
  `chat_elapsed_time` BIGINT(10) NULL,
  `chat_script_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_chat_entry_chat_script1_idx` (`chat_script_id` ASC),
  CONSTRAINT `fk_chat_entry_chat_script1`
    FOREIGN KEY (`chat_script_id`)
    REFERENCES `chat_script` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `chat_bot` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `chat_bot_name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;