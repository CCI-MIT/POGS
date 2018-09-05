-- MySQL dump 10.13  Distrib 5.7.13, for osx10.11 (x86_64)
--
-- Host: localhost    Database: pogs
-- ------------------------------------------------------
-- Server version	5.7.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auth_user`
--

DROP TABLE IF EXISTS `auth_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email_address` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(127) NOT NULL,
  `last_name` varchar(127) NOT NULL,
  `is_admin` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_user`
--

LOCK TABLES `auth_user` WRITE;
/*!40000 ALTER TABLE `auth_user` DISABLE KEYS */;
INSERT INTO `auth_user` VALUES (1,'carlosbpf@gmail.com','$2a$10$ruvXFokY4swbT2idWfk2Ge8Vce.i0iXnSbvWGdoXoH7grMnYca3gW','Carlos','Paula',NULL),
                                (2, 'admin@pogs.mit.edu', '$2a$10$3vW6KMnqv/LVEgDfRd3hkeo3WQ7h9WqbpIag0GyJzwELnrQK5Zy9m' /* password = 'pogs123' */, 'POGS', 'Admin', 0);
/*!40000 ALTER TABLE `auth_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_bot`
--

DROP TABLE IF EXISTS `chat_bot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_bot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chat_bot_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_bot`
--

LOCK TABLES `chat_bot` WRITE;
/*!40000 ALTER TABLE `chat_bot` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_bot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_channel`
--

DROP TABLE IF EXISTS `chat_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `channel_name` varchar(255) DEFAULT NULL,
  `creator_subject_id` bigint(20) DEFAULT NULL,
  `completed_task_id` bigint(20) DEFAULT NULL,
  `session_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_communication_channel_subject1_idx` (`creator_subject_id`),
  KEY `fk_communication_channel_completed_task1_idx` (`completed_task_id`),
  KEY `fk_chat_channel_session1_idx` (`session_id`),
  CONSTRAINT `fk_chat_channel_session1` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_communication_channel_completed_task1` FOREIGN KEY (`completed_task_id`) REFERENCES `completed_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_communication_channel_subject1` FOREIGN KEY (`creator_subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_channel`
--

LOCK TABLES `chat_channel` WRITE;
/*!40000 ALTER TABLE `chat_channel` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_channel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_entry`
--

DROP TABLE IF EXISTS `chat_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chat_entry_value` varchar(400) DEFAULT NULL,
  `chat_elapsed_time` bigint(10) DEFAULT NULL,
  `chat_script_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_chat_entry_chat_script1_idx` (`chat_script_id`),
  CONSTRAINT `fk_chat_entry_chat_script1` FOREIGN KEY (`chat_script_id`) REFERENCES `chat_script` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_entry`
--

LOCK TABLES `chat_entry` WRITE;
/*!40000 ALTER TABLE `chat_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_script`
--

DROP TABLE IF EXISTS `chat_script`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_script` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chat_script_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_script`
--

LOCK TABLES `chat_script` WRITE;
/*!40000 ALTER TABLE `chat_script` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_script` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `completed_task`
--

DROP TABLE IF EXISTS `completed_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `completed_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_final_snapshot` longtext,
  `round_id` bigint(20) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `subject_id` bigint(20) DEFAULT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `compled_task_order` smallint(5) DEFAULT NULL,
  `expected_start_time` datetime DEFAULT NULL,
  `expected_finish_time` datetime DEFAULT NULL,
  `inferred_end_time` varchar(45) DEFAULT NULL,
  `solo` varchar(45) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_completed_task_round1_idx` (`round_id`),
  KEY `fk_completed_task_task1_idx` (`task_id`),
  KEY `fk_completed_task_team1_idx` (`team_id`),
  KEY `fk_completed_task_subject1_idx` (`subject_id`),
  CONSTRAINT `fk_completed_task_round1` FOREIGN KEY (`round_id`) REFERENCES `round` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_completed_task_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_completed_task_task1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_completed_task_team1` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `completed_task`
--

LOCK TABLES `completed_task` WRITE;
/*!40000 ALTER TABLE `completed_task` DISABLE KEYS */;
INSERT INTO `completed_task` VALUES (78,NULL,20,3,NULL,12,NULL,1,NULL,NULL,NULL,NULL),(79,NULL,20,4,NULL,12,NULL,2,NULL,NULL,NULL,NULL),(80,NULL,20,1,NULL,12,NULL,3,NULL,NULL,NULL,NULL),(81,NULL,20,2,4,NULL,NULL,4,NULL,NULL,NULL,NULL),(82,NULL,20,2,3,NULL,NULL,4,NULL,NULL,NULL,NULL),(83,NULL,20,2,2,NULL,NULL,4,NULL,NULL,NULL,NULL),(84,NULL,20,2,1,NULL,NULL,4,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `completed_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `completed_task_attribute`
--

DROP TABLE IF EXISTS `completed_task_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `completed_task_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attribute_name` varchar(300) DEFAULT NULL,
  `string_value` longtext,
  `double_value` double DEFAULT NULL,
  `integer_value` bigint(20) DEFAULT NULL,
  `completed_task_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_completed_task_attribute_completed_task1_idx` (`completed_task_id`),
  CONSTRAINT `fk_completed_task_attribute_completed_task1` FOREIGN KEY (`completed_task_id`) REFERENCES `completed_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `completed_task_attribute`
--

LOCK TABLES `completed_task_attribute` WRITE;
/*!40000 ALTER TABLE `completed_task_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `completed_task_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_log`
--

DROP TABLE IF EXISTS `event_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_type` varchar(100) DEFAULT NULL,
  `event_content` longtext,
  `timestamp` datetime DEFAULT NULL,
  `completed_task_id` bigint(20) NOT NULL,
  `sender_subject_id` bigint(20) NOT NULL,
  `receiver_subject_id` bigint(20) DEFAULT NULL,
  `session_id` bigint(20) DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  `receiver` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_event_log_completed_task1_idx` (`completed_task_id`),
  KEY `fk_event_log_subject1_idx` (`sender_subject_id`),
  CONSTRAINT `fk_event_log_completed_task1` FOREIGN KEY (`completed_task_id`) REFERENCES `completed_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_event_log_subject1` FOREIGN KEY (`sender_subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_log`
--

LOCK TABLES `event_log` WRITE;
/*!40000 ALTER TABLE `event_log` DISABLE KEYS */;
INSERT INTO `event_log` VALUES (210,'COMMUNICATION_MESSAGE','{\"channel\":null,\"message\":\"\",\"type\":\"JOINED\"}','2018-09-03 12:52:41',78,4,NULL,1,'minsa04',NULL),(211,'COMMUNICATION_MESSAGE','{\"channel\":null,\"message\":\"\",\"type\":\"JOINED\"}','2018-09-03 12:52:41',78,3,NULL,1,'minsa03',NULL),(212,'COMMUNICATION_MESSAGE','{\"channel\":null,\"message\":\"\",\"type\":\"JOINED\"}','2018-09-03 12:52:41',78,2,NULL,1,'minsa02',NULL),(213,'COMMUNICATION_MESSAGE','{\"channel\":null,\"message\":\"\",\"type\":\"JOINED\"}','2018-09-03 12:52:42',78,1,NULL,1,'minsa01',NULL);
/*!40000 ALTER TABLE `event_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','init','SQL','V1__init.sql',1338625442,'root','2018-08-30 20:20:32',19,1),(2,'2','groupcreation','SQL','V2__groupcreation.sql',2128757585,'root','2018-08-30 20:20:32',19,1),(3,'3','groupusercreation','SQL','V3__groupusercreation.sql',-1422977129,'root','2018-08-30 20:20:33',28,1),(4,'4','studycreation','SQL','V4__studycreation.sql',1502427600,'root','2018-08-30 20:20:33',84,1),(5,'5','userisadmin','SQL','V5__userisadmin.sql',-1249476499,'root','2018-08-30 20:20:33',18,1),(6,'6','studyhasresearchgroup','SQL','V6__studyhasresearchgroup.sql',621497075,'root','2018-08-30 20:20:33',3,1),(7,'7','conditionsession','SQL','V7__conditionsession.sql',788644067,'root','2018-08-30 20:20:33',63,1),(8,'8','sessionalter','SQL','V8__sessionalter.sql',982330076,'root','2018-08-30 20:20:33',33,1),(9,'9','feedbackwidget','SQL','V9__feedbackwidget.sql',-523975480,'root','2018-08-30 20:20:33',123,1),(10,'10','taskandtaskgroup','SQL','V10__taskandtaskgroup.sql',790176784,'root','2018-08-30 20:20:33',58,1),(11,'11','taskhasresearchgroup','SQL','V11__taskhasresearchgroup.sql',159541609,'root','2018-08-30 20:20:33',25,1),(12,'12','taskhasresearchgroupbigint','SQL','V12__taskhasresearchgroupbigint.sql',-1274676862,'root','2018-08-30 20:20:33',67,1),(13,'13','sessionhastaskgroup','SQL','V13__sessionhastaskgroup.sql',-1011386481,'root','2018-08-30 20:20:33',23,1),(14,'14','communicatio','SQL','V14__communicatio.sql',-1003192071,'root','2018-08-30 20:20:33',60,1),(15,'15','subject and attributes','SQL','V15__subject_and_attributes.sql',166433909,'root','2018-08-30 20:20:33',56,1),(16,'16','session number of rounds','SQL','V16__session_number_of_rounds.sql',1455800448,'root','2018-08-30 20:20:33',27,1),(17,'17','subject session','SQL','V17__subject_session.sql',496614416,'root','2018-08-30 20:20:33',80,1),(18,'18','chatbot id','SQL','V18__chatbot_id.sql',271205994,'root','2018-08-30 20:20:34',22,1),(19,'19','rounds','SQL','V19__rounds.sql',-1188294185,'root','2018-08-30 20:20:34',21,1),(20,'20','teamandcompletedtask','SQL','V20__teamandcompletedtask.sql',1885120052,'root','2018-08-30 20:20:34',103,1),(21,'21','teamhassubject','SQL','V21__teamhassubject.sql',1442534851,'root','2018-08-30 20:20:34',19,1),(22,'22','taskconfig execution attr','SQL','V22__taskconfig_execution_attr.sql',-1845333992,'root','2018-08-30 20:20:34',48,1),(23,'23','task has task config','SQL','V23__task_has_task_config.sql',-593849762,'root','2018-08-30 20:20:34',39,1),(24,'24','config attribute pk','SQL','V24__config_attribute_pk.sql',-1840686687,'root','2018-08-30 20:20:34',33,1),(25,'25','event log','SQL','V25__event_log.sql',-1529814772,'root','2018-08-30 20:20:34',31,1),(26,'26','subject communication','SQL','V26__subject_communication.sql',397525334,'root','2018-08-30 20:20:34',18,1),(27,'27','taskconfiguration','SQL','V27__taskconfiguration.sql',1636821525,'root','2018-08-30 20:20:34',27,1),(28,'28','chatchannel','SQL','V28__chatchannel.sql',-150564287,'root','2018-08-30 20:20:34',36,1),(29,'29','sessionfixedtime','SQL','V29__sessionfixedtime.sql',-99963683,'root','2018-08-30 20:20:34',27,1),(30,'30','eventlogchange','SQL','V30__eventlogchange.sql',1059882392,'root','2018-08-30 20:20:34',134,1),(31,'31','todoentrydelete','SQL','V31__todoentrydelete.sql',33132214,'root','2018-08-30 20:20:34',20,1),(32,'32','conditionremoval','SQL','V32__conditionremoval.sql',731838551,'root','2018-08-30 20:20:34',112,1),(33,'33','conditiondrop','SQL','V33__conditiondrop.sql',-1970347485,'root','2018-08-30 20:36:29',23,1),(34,'35','videoprimer','SQL','V35__videoprimer.sql',1415617288,'root','2018-08-30 22:17:23',46,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `research_group`
--

DROP TABLE IF EXISTS `research_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `research_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `research_group`
--

LOCK TABLES `research_group` WRITE;
/*!40000 ALTER TABLE `research_group` DISABLE KEYS */;
INSERT INTO `research_group` VALUES (1,'admin-research-group'),(2,'non-admin-researchgroup');
/*!40000 ALTER TABLE `research_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `research_group_has_auth_user`
--

DROP TABLE IF EXISTS `research_group_has_auth_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `research_group_has_auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `research_group_id` bigint(20) NOT NULL,
  `auth_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_research_group_has_auth_user_research_group1_idx` (`research_group_id`),
  KEY `fk_research_group_has_auth_user_auth_user1_idx` (`auth_user_id`),
  CONSTRAINT `fk_research_group_has_auth_user_auth_user1` FOREIGN KEY (`auth_user_id`) REFERENCES `auth_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_research_group_has_auth_user_research_group1` FOREIGN KEY (`research_group_id`) REFERENCES `research_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `research_group_has_auth_user`
--

LOCK TABLES `research_group_has_auth_user` WRITE;
/*!40000 ALTER TABLE `research_group_has_auth_user` DISABLE KEYS */;
INSERT INTO `research_group_has_auth_user` VALUES (1,1,1),
                                                  (1,1,2);
/*!40000 ALTER TABLE `research_group_has_auth_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `round`
--

DROP TABLE IF EXISTS `round`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `round` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `round_number` int(11) DEFAULT NULL,
  `session_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_round_session1_idx` (`session_id`),
  CONSTRAINT `fk_round_session1` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `round`
--

LOCK TABLES `round` WRITE;
/*!40000 ALTER TABLE `round` DISABLE KEYS */;
INSERT INTO `round` VALUES (20,1,1);
/*!40000 ALTER TABLE `round` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `session_suffix` varchar(45) DEFAULT NULL,
  `session_start_date` datetime DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `waiting_room_time` int(11) DEFAULT NULL,
  `intro_page_enabled` tinyint(4) DEFAULT NULL,
  `intro_text` longtext,
  `intro_time` int(11) DEFAULT NULL,
  `display_name_change_page_enabled` tinyint(4) DEFAULT NULL,
  `display_name_change_time` int(11) DEFAULT NULL,
  `roster_page_enabled` tinyint(4) DEFAULT NULL,
  `roster_time` int(11) DEFAULT NULL,
  `done_page_enabled` tinyint(4) DEFAULT NULL,
  `done_page_text` longtext,
  `done_page_time` int(11) DEFAULT NULL,
  `done_redirect_url` varchar(400) DEFAULT NULL,
  `could_not_assign_to_team_message` varchar(400) DEFAULT NULL,
  `task_execution_type` char(1) DEFAULT NULL,
  `rounds_enabled` tinyint(4) DEFAULT NULL,
  `number_of_rounds` int(11) DEFAULT NULL,
  `communication_type` char(1) DEFAULT NULL,
  `chat_bot_name` varchar(255) DEFAULT NULL,
  `scoreboard_enabled` tinyint(4) DEFAULT NULL,
  `scoreboard_display_type` char(1) DEFAULT NULL,
  `scoreboard_use_display_names` tinyint(4) DEFAULT NULL,
  `collaboration_todo_list_enabled` tinyint(4) DEFAULT NULL,
  `collaboration_feedback_widget_enabled` tinyint(4) DEFAULT NULL,
  `collaboration_voting_widget_enabled` tinyint(4) DEFAULT NULL,
  `team_creation_moment` char(1) DEFAULT NULL,
  `team_creation_type` char(1) DEFAULT NULL,
  `team_min_size` int(11) DEFAULT NULL,
  `team_max_size` int(11) DEFAULT NULL,
  `team_creation_method` char(1) DEFAULT NULL,
  `team_creation_matrix` longtext,
  `fixed_interaction_time` int(11) DEFAULT NULL,
  `study_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_study_id` (`study_id`),
  CONSTRAINT `fk_study_id` FOREIGN KEY (`study_id`) REFERENCES `study` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session`
--

LOCK TABLES `session` WRITE;
/*!40000 ALTER TABLE `session` DISABLE KEYS */;
INSERT INTO `session` VALUES (1,'s01','2018-09-03 12:51:00','S',20,1,'<h3 style=\"text-align: center; \">Welcome to the test study</h3><p style=\"text-align: center;\"><br></p><p style=\"text-align: center;\">You will be asked to work on 3 tasks,</p><p style=\"text-align: center;\">feel free to coordinate with the other subjects.</p><p style=\"text-align: center;\"><br></p>',60,1,20,1,20,1,'<h2 style=\"text-align: center; \">Thanks for your help!</h2><p style=\"text-align: center;\"><br></p><p style=\"text-align: center;\">Your contribution is essential to our studies.</p>',100,'http://www.google.com','Sorry we could not assign you to a team, thanks for the help.','S',1,1,'N','',0,'T',0,0,0,0,'S','S',4,NULL,'C','',0,1);
/*!40000 ALTER TABLE `session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session_has_task_group`
--

DROP TABLE IF EXISTS `session_has_task_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_has_task_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `session_id` bigint(20) NOT NULL,
  `task_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_session_has_task_group_session1_idx` (`session_id`),
  KEY `fk_session_has_task_group_task_group1_idx` (`task_group_id`),
  CONSTRAINT `fk_session_has_task_group_session1` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_session_has_task_group_task_group1` FOREIGN KEY (`task_group_id`) REFERENCES `task_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session_has_task_group`
--

LOCK TABLES `session_has_task_group` WRITE;
/*!40000 ALTER TABLE `session_has_task_group` DISABLE KEYS */;
INSERT INTO `session_has_task_group` VALUES (1,1,1);
/*!40000 ALTER TABLE `session_has_task_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study`
--

DROP TABLE IF EXISTS `study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `study` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `study_name` varchar(255) DEFAULT NULL,
  `study_description` varchar(45) DEFAULT NULL,
  `study_session_prefix` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study`
--

LOCK TABLES `study` WRITE;
/*!40000 ALTER TABLE `study` DISABLE KEYS */;
INSERT INTO `study` VALUES (1,'Test study','test study','tst');
/*!40000 ALTER TABLE `study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_has_research_group`
--

DROP TABLE IF EXISTS `study_has_research_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `study_has_research_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `study_id` bigint(20) NOT NULL,
  `research_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_study_has_research_group_study1_idx` (`study_id`),
  KEY `fk_study_has_research_group_research_group1_idx` (`research_group_id`),
  CONSTRAINT `fk_study_has_research_group_research_group1` FOREIGN KEY (`research_group_id`) REFERENCES `research_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_study_has_research_group_study1` FOREIGN KEY (`study_id`) REFERENCES `study` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_has_research_group`
--

LOCK TABLES `study_has_research_group` WRITE;
/*!40000 ALTER TABLE `study_has_research_group` DISABLE KEYS */;
INSERT INTO `study_has_research_group` VALUES (1,1,1);
/*!40000 ALTER TABLE `study_has_research_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subject_external_id` varchar(255) DEFAULT NULL,
  `subject_display_name` varchar(45) DEFAULT NULL,
  `session_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  CONSTRAINT `subject_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (1,'minsa01','carlos',1),(2,'minsa02','bruno',1),(3,'minsa03','min',1),(4,'minsa04','santha',1);
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject_attribute`
--

DROP TABLE IF EXISTS `subject_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attribute_name` varchar(300) DEFAULT NULL COMMENT '	',
  `string_value` longtext,
  `integer_value` bigint(20) DEFAULT NULL,
  `real_value` double DEFAULT NULL,
  `subject_id` bigint(20) NOT NULL,
  `latest` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_subject_attribute_subject1_idx` (`subject_id`),
  CONSTRAINT `fk_subject_attribute_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject_attribute`
--

LOCK TABLES `subject_attribute` WRITE;
/*!40000 ALTER TABLE `subject_attribute` DISABLE KEYS */;
INSERT INTO `subject_attribute` VALUES (97,'SUBJECT_DEFAULT_BACKGROUND_COLOR','#e31a97',NULL,NULL,3,NULL),(98,'SUBJECT_DEFAULT_FONT_COLOR','#ffffff',NULL,NULL,3,NULL),(99,'SUBJECT_DEFAULT_BACKGROUND_COLOR','#e2e749',NULL,NULL,4,NULL),(100,'SUBJECT_DEFAULT_FONT_COLOR','#000',NULL,NULL,4,NULL),(101,'SUBJECT_DEFAULT_BACKGROUND_COLOR','#1ff696',NULL,NULL,2,NULL),(102,'SUBJECT_DEFAULT_FONT_COLOR','#000',NULL,NULL,2,NULL),(103,'SUBJECT_DEFAULT_BACKGROUND_COLOR','#5522df',NULL,NULL,1,NULL),(104,'SUBJECT_DEFAULT_FONT_COLOR','#ffffff',NULL,NULL,1,NULL);
/*!40000 ALTER TABLE `subject_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject_communication`
--

DROP TABLE IF EXISTS `subject_communication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject_communication` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allowed` tinyint(4) DEFAULT '0',
  `from_subject_id` bigint(20) NOT NULL,
  `to_subject_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_subject_communication_subject_idx` (`from_subject_id`),
  KEY `fk_subject_communication_subject1_idx` (`to_subject_id`),
  CONSTRAINT `fk_subject_communication_subject` FOREIGN KEY (`from_subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_subject_communication_subject1` FOREIGN KEY (`to_subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject_communication`
--

LOCK TABLES `subject_communication` WRITE;
/*!40000 ALTER TABLE `subject_communication` DISABLE KEYS */;
INSERT INTO `subject_communication` VALUES (1,1,1,1),(2,1,1,2),(3,1,1,3),(4,1,1,4),(5,1,2,1),(6,1,2,2),(7,1,2,3),(8,1,2,4),(9,1,3,1),(10,1,3,2),(11,1,3,3),(12,1,3,4),(13,1,4,1),(14,1,4,2),(15,1,4,3),(16,1,4,4);
/*!40000 ALTER TABLE `subject_communication` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject_has_channel`
--

DROP TABLE IF EXISTS `subject_has_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject_has_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `subject_id` bigint(20) NOT NULL,
  `added_by_subject_id` bigint(20) DEFAULT NULL,
  `chat_channel_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_subject_has_channel_subject1_idx` (`subject_id`),
  KEY `fk_subject_has_channel_subject2_idx` (`added_by_subject_id`),
  KEY `fk_subject_has_channel_communication_channel1_idx` (`chat_channel_id`),
  CONSTRAINT `fk_subject_has_channel_communication_channel1` FOREIGN KEY (`chat_channel_id`) REFERENCES `chat_channel` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_subject_has_channel_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_subject_has_channel_subject2` FOREIGN KEY (`added_by_subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject_has_channel`
--

LOCK TABLES `subject_has_channel` WRITE;
/*!40000 ALTER TABLE `subject_has_channel` DISABLE KEYS */;
/*!40000 ALTER TABLE `subject_has_channel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_name` varchar(255) DEFAULT NULL,
  `task_plugin_type` varchar(45) NOT NULL,
  `solo_task` tinyint(4) DEFAULT NULL,
  `interaction_time` int(11) DEFAULT NULL,
  `intro_page_enabled` tinyint(4) DEFAULT NULL,
  `intro_text` longtext,
  `intro_time` int(11) DEFAULT NULL,
  `primer_page_enabled` tinyint(4) DEFAULT NULL,
  `primer_text` longtext,
  `primer_video_autoplay_mute` tinyint(4) DEFAULT NULL,
  `primer_time` int(11) DEFAULT NULL,
  `interaction_widget_enabled` tinyint(4) DEFAULT NULL,
  `interaction_text` longtext,
  `communication_type` char(1) DEFAULT NULL,
  `collaboration_todo_list_enabled` tinyint(4) DEFAULT NULL,
  `collaboration_feedback_widget_enabled` tinyint(4) DEFAULT NULL,
  `collaboration_voting_widget_enabled` tinyint(4) DEFAULT NULL,
  `scoring_type` char(1) DEFAULT NULL,
  `subject_communication_id` bigint(20) DEFAULT NULL,
  `chat_script_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,'Sudoku task ','sudokuPlugin',0,120,1,'<p style=\"text-align: center; \">The next task is a sudoku solving puzzle.</p><p style=\"text-align: center; \">Use the Dyadic chat to talk to coordinate.</p>',120,0,'',0,0,0,'','D',1,0,1,'S',NULL,NULL),(2,'Survey Task','surveyPlugin',1,120,1,'<p style=\"text-align: center; \">The next task is an individual survey.</p><p style=\"text-align: center; \">Take your time to answer all items.</p>',120,1,'<p><iframe frameborder=\"0\" src=\"//www.youtube.com/embed/IidgdlSKf_k?autoplay=1&mute=1\" width=\"640\" height=\"360\" class=\"note-video-clip\"></iframe><br></p>',1,200,0,'','N',0,0,0,'S',NULL,NULL),(3,'Resting area','surveyPlugin',0,120,0,'',0,0,'',0,0,0,'','G',0,0,0,'S',NULL,NULL),(4,'Text typing task','typingPlugin',0,4000,1,'<h2 style=\"text-align: center; \">Please type the text in the pad area</h2><p style=\"text-align: center;\">Feel free to coordinate with other subjects using the chat.</p>',130,0,'',0,0,1,'<p>Tirades lalalalala</p>','G',0,0,0,'S',NULL,NULL);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_configuration`
--

DROP TABLE IF EXISTS `task_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `configuration_name` varchar(45) DEFAULT NULL,
  `task_plugin_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_configuration`
--

LOCK TABLES `task_configuration` WRITE;
/*!40000 ALTER TABLE `task_configuration` DISABLE KEYS */;
INSERT INTO `task_configuration` VALUES (1,'simpleSudoku','sudokuPlugin'),(2,'resting area','surveyPlugin'),(3,'surveyTaskIndividual','surveyPlugin'),(5,'typingPluginConfig','typingPlugin');
/*!40000 ALTER TABLE `task_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_execution_attribute`
--

DROP TABLE IF EXISTS `task_execution_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_execution_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attribute_name` varchar(300) DEFAULT NULL,
  `string_value` longtext,
  `integer_value` bigint(20) DEFAULT NULL,
  `double_value` double DEFAULT NULL,
  `task_configuration_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_task_execution_attribute_task_configuration1_idx` (`task_configuration_id`),
  CONSTRAINT `fk_task_execution_attribute_task_configuration1` FOREIGN KEY (`task_configuration_id`) REFERENCES `task_configuration` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_execution_attribute`
--

LOCK TABLES `task_execution_attribute` WRITE;
/*!40000 ALTER TABLE `task_execution_attribute` DISABLE KEYS */;
INSERT INTO `task_execution_attribute` VALUES (5,'surveyBluePrint','[{\"question\":\"Did you like the task? Please justify your answer!\",\"type\":\"text\",\"placeholder\":\"\"},{\"question\":\"Did one of your teammates play a leading role?\",\"type\":\"radio\",\"value\":[\"carlos\",\"min\",\"santha\",\"bruno\"]}]',NULL,NULL,3),(6,'answerSheet','[\"\",\"carlos\"]',NULL,NULL,3),(7,'surveyBluePrint','[{\"question\":\"<p style=\\\"text-align: center; \\\">This is the resting area for the multi task session.</p><p style=\\\"text-align: center; \\\"><span style=\\\"font-family: -apple-system, BlinkMacSystemFont, &quot;Segoe UI&quot;, Roboto, &quot;Helvetica Neue&quot;, Arial, sans-serif, &quot;Apple Color Emoji&quot;, &quot;Segoe UI Emoji&quot;, &quot;Segoe UI Symbol&quot;; font-size: 1rem;\\\">Feel free to connect using the group chat. Use the tabs to navigate between the tasks.</span></p>\",\"type\":\"introduction\"}]',NULL,NULL,2),(8,'answerSheet','[]',NULL,NULL,2),(9,'gridBluePrint','5,8,0,2,1,3,9,6,4,2,9,0,0,0,6,3,1,7,3,0,1,4,7,9,2,5,8,4,1,0,7,0,8,5,9,3,6,0,0,3,9,1,0,0,2,0,7,3,5,2,0,1,8,6,0,2,0,9,8,5,4,3,0,1,4,0,6,3,2,0,7,0,8,3,9,1,4,7,6,2,5',NULL,NULL,1),(10,'answerSheet','5,8,7,2,1,3,9,6,4,2,9,4,8,5,6,3,1,7,3,6,1,4,7,9,2,5,8,4,1,2,7,6,8,5,9,3,6,5,8,3,9,1,7,4,2,9,7,3,5,2,4,1,8,6,7,2,6,9,8,5,4,3,1,1,4,5,6,3,2,8,7,9,8,3,9,1,4,7,6,2,5',NULL,NULL,1);
/*!40000 ALTER TABLE `task_execution_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_group`
--

DROP TABLE IF EXISTS `task_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_group_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_group`
--

LOCK TABLES `task_group` WRITE;
/*!40000 ALTER TABLE `task_group` DISABLE KEYS */;
INSERT INTO `task_group` VALUES (1,'taskGroup');
/*!40000 ALTER TABLE `task_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_group_has_task`
--

DROP TABLE IF EXISTS `task_group_has_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_group_has_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order` int(11) DEFAULT NULL,
  `task_id` bigint(20) NOT NULL,
  `task_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_task_group_has_task_task1_idx` (`task_id`),
  KEY `fk_task_group_has_task_task_group1_idx` (`task_group_id`),
  CONSTRAINT `fk_task_group_has_task_task1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_group_has_task_task_group1` FOREIGN KEY (`task_group_id`) REFERENCES `task_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_group_has_task`
--

LOCK TABLES `task_group_has_task` WRITE;
/*!40000 ALTER TABLE `task_group_has_task` DISABLE KEYS */;
INSERT INTO `task_group_has_task` VALUES (1,0,3,1),(2,2,1,1),(3,3,2,1),(4,1,4,1);
/*!40000 ALTER TABLE `task_group_has_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_has_research_group`
--

DROP TABLE IF EXISTS `task_has_research_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_has_research_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `research_group_id` bigint(20) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_task_has_research_group_research_group1_idx` (`research_group_id`),
  KEY `fk_task_has_research_group_task1_idx` (`task_id`),
  CONSTRAINT `fk_task_has_research_group_research_group1` FOREIGN KEY (`research_group_id`) REFERENCES `research_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_has_research_group_task1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_has_research_group`
--

LOCK TABLES `task_has_research_group` WRITE;
/*!40000 ALTER TABLE `task_has_research_group` DISABLE KEYS */;
INSERT INTO `task_has_research_group` VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4);
/*!40000 ALTER TABLE `task_has_research_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_has_task_configuration`
--

DROP TABLE IF EXISTS `task_has_task_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_has_task_configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) NOT NULL,
  `task_configuration_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_task_has_task_configuration_task1_idx` (`task_id`),
  KEY `fk_task_has_task_configuration_task_configuration1_idx` (`task_configuration_id`),
  CONSTRAINT `fk_task_has_task_configuration_task1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_task_has_task_configuration_task_configuration1` FOREIGN KEY (`task_configuration_id`) REFERENCES `task_configuration` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_has_task_configuration`
--

LOCK TABLES `task_has_task_configuration` WRITE;
/*!40000 ALTER TABLE `task_has_task_configuration` DISABLE KEYS */;
INSERT INTO `task_has_task_configuration` VALUES (1,1,1),(3,3,2),(4,2,3),(5,4,5);
/*!40000 ALTER TABLE `task_has_task_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `session_id` bigint(20) NOT NULL,
  `round_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_team_session1_idx` (`session_id`),
  KEY `fk_team_round1_idx` (`round_id`),
  KEY `fk_team_task1_idx` (`task_id`),
  CONSTRAINT `fk_team_round1` FOREIGN KEY (`round_id`) REFERENCES `round` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_session1` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_task1` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` VALUES (12,1,20,NULL);
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_has_subject`
--

DROP TABLE IF EXISTS `team_has_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team_has_subject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` bigint(20) NOT NULL,
  `subject_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_team_has_subject_team1_idx` (`team_id`),
  KEY `fk_team_has_subject_subject1_idx` (`subject_id`),
  CONSTRAINT `fk_team_has_subject_subject1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_has_subject_team1` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_has_subject`
--

LOCK TABLES `team_has_subject` WRITE;
/*!40000 ALTER TABLE `team_has_subject` DISABLE KEYS */;
INSERT INTO `team_has_subject` VALUES (45,12,3),(46,12,4),(47,12,2),(48,12,1);
/*!40000 ALTER TABLE `team_has_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `todo_entry`
--

DROP TABLE IF EXISTS `todo_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `todo_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` varchar(400) DEFAULT NULL,
  `todo_entry_date` datetime DEFAULT NULL,
  `marked_done_date` datetime DEFAULT NULL,
  `marked_done` tinyint(4) DEFAULT NULL,
  `completed_task_id` bigint(20) NOT NULL,
  `creator_id` bigint(20) NOT NULL,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `todo_entry`
--

LOCK TABLES `todo_entry` WRITE;
/*!40000 ALTER TABLE `todo_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `todo_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `todo_entry_assignment`
--

DROP TABLE IF EXISTS `todo_entry_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `todo_entry_assignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assignment_date` datetime DEFAULT NULL,
  `current_assigned` tinyint(4) DEFAULT NULL,
  `todo_entry_id` bigint(20) NOT NULL,
  `subject_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_todo_entry_assignment_todo_entry1_idx` (`todo_entry_id`),
  CONSTRAINT `fk_todo_entry_assignment_todo_entry1` FOREIGN KEY (`todo_entry_id`) REFERENCES `todo_entry` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `todo_entry_assignment`
--

LOCK TABLES `todo_entry_assignment` WRITE;
/*!40000 ALTER TABLE `todo_entry_assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `todo_entry_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voting_pool`
--

DROP TABLE IF EXISTS `voting_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voting_pool` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `voting_question` varchar(255) DEFAULT NULL,
  `completed_task_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voting_pool`
--

LOCK TABLES `voting_pool` WRITE;
/*!40000 ALTER TABLE `voting_pool` DISABLE KEYS */;
/*!40000 ALTER TABLE `voting_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voting_pool_option`
--

DROP TABLE IF EXISTS `voting_pool_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voting_pool_option` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `voting_option` varchar(255) DEFAULT NULL,
  `voting_pool_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_voting_pool_option_voting_pool1_idx` (`voting_pool_id`),
  CONSTRAINT `fk_voting_pool_option_voting_pool1` FOREIGN KEY (`voting_pool_id`) REFERENCES `voting_pool` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voting_pool_option`
--

LOCK TABLES `voting_pool_option` WRITE;
/*!40000 ALTER TABLE `voting_pool_option` DISABLE KEYS */;
/*!40000 ALTER TABLE `voting_pool_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voting_pool_vote`
--

DROP TABLE IF EXISTS `voting_pool_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voting_pool_vote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `voting_pool_option_id` bigint(20) NOT NULL,
  `subject_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_voting_pool_vote_voting_pool_option1_idx` (`voting_pool_option_id`),
  CONSTRAINT `fk_voting_pool_vote_voting_pool_option1` FOREIGN KEY (`voting_pool_option_id`) REFERENCES `voting_pool_option` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voting_pool_vote`
--

LOCK TABLES `voting_pool_vote` WRITE;
/*!40000 ALTER TABLE `voting_pool_vote` DISABLE KEYS */;
/*!40000 ALTER TABLE `voting_pool_vote` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-09-03 13:28:58
