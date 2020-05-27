ALTER TABLE `subject`
    ADD COLUMN `pogs_unique_hash` VARCHAR(40) NULL AFTER `previous_session_subject`;
