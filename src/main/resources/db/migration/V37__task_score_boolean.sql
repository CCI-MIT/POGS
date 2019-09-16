ALTER TABLE `task` 
ADD COLUMN `should_score` TINYINT(4) NOT NULL DEFAULT 0 AFTER `chat_script_id`;
