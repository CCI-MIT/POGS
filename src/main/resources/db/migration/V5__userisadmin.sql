ALTER TABLE `auth_user`
ADD COLUMN `is_admin` TINYINT NULL DEFAULT 0 AFTER `last_name`;
