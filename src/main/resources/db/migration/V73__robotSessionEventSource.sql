ALTER TABLE `session`
    ADD COLUMN `robot_session_event_source_id` BIGINT(20) NULL AFTER `scoreboard_average_solo_session`,
    ADD COLUMN `robot_session_event_script_id` BIGINT(20) NULL AFTER `robot_session_event_source_id`;
