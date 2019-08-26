CREATE TABLE IF NOT EXISTS `session_execution_attribute` (
        `id` BIGINT(10) NOT NULL AUTO_INCREMENT,
        `attribute_name` VARCHAR(300) NULL,
        `integer_value` BIGINT(20) NULL,
        `double_value` DOUBLE NULL,
        `string_value` LONGTEXT NULL,
        `session_id` BIGINT NOT NULL,
        PRIMARY KEY (`id`),
        INDEX `fk_session_execution_attribute_session1_idx` (`session_id` ASC),
        CONSTRAINT `fk_session_execution_attribute_session1`
          FOREIGN KEY (`session_id`)
            REFERENCES `session` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION)
  ENGINE = InnoDB;