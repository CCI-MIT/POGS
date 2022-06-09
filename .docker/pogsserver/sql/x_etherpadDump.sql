CREATE DATABASE etherpad_lite_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER 'etherpad'@'%' IDENTIFIED BY 'a1234';
GRANT ALL PRIVILEGES ON etherpad_lite_db.* TO 'etherpad'@'%';
FLUSH PRIVILEGES;