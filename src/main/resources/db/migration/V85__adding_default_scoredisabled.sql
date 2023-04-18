ALTER TABLE `pogs`.`task`
    CHANGE COLUMN `score_page_enabled` `score_page_enabled` TINYINT NULL DEFAULT 0 ;

UPDATE `pogs`.`task` SET `score_page_enabled` = '0';
