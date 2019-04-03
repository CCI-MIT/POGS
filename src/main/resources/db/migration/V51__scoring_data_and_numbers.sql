ALTER TABLE `completed_task_score`
  ADD COLUMN `number_of_right_answers` INT NULL AFTER `completed_task_id`,
  ADD COLUMN `number_of_wrong_answers` INT NULL AFTER `number_of_right_answers`,
  ADD COLUMN `number_of_entries` INT NULL AFTER `number_of_wrong_answers`,
  ADD COLUMN `number_of_processed_entries` INT NULL AFTER `number_of_entries`,
  ADD COLUMN `scoring_data` LONGTEXT NULL AFTER `number_of_processed_entries`;
