ALTER TABLE `unprocessed_dictionary_entry`
  ADD COLUMN `entry_type` CHAR(1) NULL DEFAULT 'C' AFTER `has_been_processed`;
