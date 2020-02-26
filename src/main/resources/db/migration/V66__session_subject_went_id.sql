ALTER TABLE subject_has_session_check_in
    ADD COLUMN `joined_session_id` BIGINT(20) NULL AFTER `subject_id`;
