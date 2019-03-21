alter table task_configuration
  add before_work_script_id BIGINT null;

alter table task_configuration
  add after_work_script_id BIGINT null;

alter table task_configuration
  add score_script_id BIGINT null;

