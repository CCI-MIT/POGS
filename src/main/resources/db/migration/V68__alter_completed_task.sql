ALTER TABLE `event_log`
    DROP FOREIGN KEY `fk_event_log_completed_task1`;
ALTER TABLE `event_log`
    CHANGE COLUMN `completed_task_id` `completed_task_id` BIGINT(20) NULL ;
ALTER TABLE `event_log`
    ADD CONSTRAINT `fk_event_log_completed_task1`
        FOREIGN KEY (`completed_task_id`)
            REFERENCES `completed_task` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;
