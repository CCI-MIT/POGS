ALTER TABLE `todo_entry` 
ADD COLUMN `deleted_at` DATETIME NULL AFTER `creator_id`;
