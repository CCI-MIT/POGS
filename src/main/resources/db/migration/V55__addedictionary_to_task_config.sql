ALTER TABLE `task_configuration`
  ADD COLUMN `dictionary_id` BIGINT(20) NULL DEFAULT NULL AFTER `score_script_id`;
