CREATE TABLE IF NOT EXISTS `round` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `round_number` INT NULL,
  `session_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_round_session1_idx` (`session_id`),
  CONSTRAINT `fk_round_session1`
    FOREIGN KEY (`session_id`)
    REFERENCES `session` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB