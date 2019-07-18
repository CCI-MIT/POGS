ALTER TABLE `session`
  ADD COLUMN `display_name_generation_enabled` TINYINT(4) NULL AFTER `session_wide_script_id`;
