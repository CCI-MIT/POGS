ALTER TABLE `session`
  ADD COLUMN `session_wide_script_id` BIGINT(20) NULL DEFAULT NULL AFTER `executable_script_id`;
