ALTER TABLE `auth_user`
ADD COLUMN `is_admin` TINYINT NOT NULL DEFAULT 0 AFTER `last_name`;
