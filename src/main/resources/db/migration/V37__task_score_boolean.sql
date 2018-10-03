ALTER TABLE `task` 
ADD COLUMN `should_score` TINYINT(4) NULL AFTER `chat_script_id`;
