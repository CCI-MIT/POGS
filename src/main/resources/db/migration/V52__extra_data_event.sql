ALTER TABLE `event_log`
  ADD COLUMN `extra_data` TEXT NULL AFTER `receiver`;
