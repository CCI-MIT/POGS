alter table subject
  add previous_session_subject bigint default NULL null;
alter table session
  add full_session_name VARCHAR(255) null;
