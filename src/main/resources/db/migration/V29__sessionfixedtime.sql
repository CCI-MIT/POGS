ALTER TABLE `session`
ADD COLUMN `fixed_interaction_time` INT(11) NOT NULL DEFAULT 0 AFTER `team_creation_matrix`;
