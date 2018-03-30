CREATE TABLE auth_user (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`email_address` varchar(255) not null,
	`password` varchar(255) not null,
	`first_name` varchar(127) not null,
	`last_name` varchar(127) not null,
	primary key(`id`)
);
