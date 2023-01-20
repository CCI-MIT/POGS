ALTER TABLE `pogs`.`session`
    ADD COLUMN `waiting_room_message` VARCHAR(355) NULL AFTER `trigger_task_for_video_chat`;
