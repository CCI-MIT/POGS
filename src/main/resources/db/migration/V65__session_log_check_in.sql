CREATE TABLE IF NOT EXISTS `subject_has_session_check_in` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `check_in_time` DATETIME NULL,
     `last_ping_time` DATETIME NULL,
     `should_expire_time` DATETIME NULL,
     `lost_subject_time` DATETIME NULL,
     `joined_session_time` DATETIME NULL,
     `has_joined_session` TINYINT NULL DEFAULT 0,
     `has_lost_session` TINYINT NULL DEFAULT 0,
     `session_id` BIGINT NOT NULL,
     `subject_id` BIGINT NOT NULL,
     PRIMARY KEY (`id`),
     INDEX `fk_subject_has_session_check_in_session1_idx` (`session_id` ASC),
     INDEX `fk_subject_has_session_check_in_subject1_idx` (`subject_id` ASC),
     CONSTRAINT `fk_subject_has_session_check_in_session1`
         FOREIGN KEY (`session_id`)
             REFERENCES `session` (`id`)
             ON DELETE NO ACTION
             ON UPDATE NO ACTION,
     CONSTRAINT `fk_subject_has_session_check_in_subject1`
         FOREIGN KEY (`subject_id`)
             REFERENCES `subject` (`id`)
             ON DELETE NO ACTION
             ON UPDATE NO ACTION)
    ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `session_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `log_time` DATETIME NULL,
    `log_type` VARCHAR(10) NULL,
    `message` TEXT NULL,
    `session_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_session_log_session1_idx` (`session_id` ASC),
    CONSTRAINT `fk_session_log_session1`
        FOREIGN KEY (`session_id`)
            REFERENCES `session` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION)
    ENGINE = InnoDB;