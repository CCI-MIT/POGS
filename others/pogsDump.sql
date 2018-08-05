SET FOREIGN_KEY_CHECKS=0;

LOCK TABLES `auth_user` WRITE;
/*!40000 ALTER TABLE `auth_user` DISABLE KEYS */;
INSERT INTO `auth_user` VALUES (1,'carlosbpf@gmail.com','$2a$10$flGbMCqxvwKvZkPi59nkSuLgbfa6sVrsJxdvEacncFVTZJpNew4hq','Carlos','Botelho',1),(2,'admin@pogs.edu','$2a$10$CH5Aj3LajF8qvcAIJhE5Xe4e0weXV.YuXhJYNKc/qIpOw7sCZYxJy','Admin','Pogs',1);
/*!40000 ALTER TABLE `auth_user` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `completed_task` WRITE;
/*!40000 ALTER TABLE `completed_task` DISABLE KEYS */;
INSERT INTO `completed_task` VALUES (111,NULL,196,2,NULL,110,NULL,1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `completed_task` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `condition` WRITE;
/*!40000 ALTER TABLE `condition` DISABLE KEYS */;
INSERT INTO `condition` VALUES (1,'condition 1',1),(2,'condition2',3),(3,'condition3',4);
/*!40000 ALTER TABLE `condition` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `research_group` WRITE;
/*!40000 ALTER TABLE `research_group` DISABLE KEYS */;
INSERT INTO `research_group` VALUES (1,'admin-group');
/*!40000 ALTER TABLE `research_group` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `research_group_has_auth_user` WRITE;
/*!40000 ALTER TABLE `research_group_has_auth_user` DISABLE KEYS */;
INSERT INTO `research_group_has_auth_user` VALUES (1,1,1),(2,1,2),(3,1,2);
/*!40000 ALTER TABLE `research_group_has_auth_user` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `session` WRITE;
/*!40000 ALTER TABLE `session` DISABLE KEYS */;
INSERT INTO `session` VALUES (1,'s01','2018-06-26 23:00:00',1,'S',300,1,'Intro page',10,1,10,1,10,1,'Thanks for this session',100,'http://www.google.com.br','Could not assign all people to groups ','S',1,1,'G','carlos_bot',0,'T',0,1,0,1,'S','C',2,NULL,'C',''),(2,'s02','2018-06-27 01:04:00',2,'S',300,1,'This is the intro',10,1,10,1,10,1,'Thanks',100,'http://www.google.com.br','Could not assign all people to groups ','S',1,1,'G','',0,'T',0,0,0,0,'S','C',2,NULL,'C','');
/* for wack-a-mole plugin */
INSERT INTO `session` VALUES (3,'s03','2018-08-03 23:00:00',3,'S',300,1,'Intro page',10,1,10,1,10,1,'Thanks for this session',100,'http://www.google.com.br','Could not assign all people to groups ','S',1,1,'G','',0,'T',0,1,0,1,'S','C',2,NULL,'C','');
/*!40000 ALTER TABLE `session` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `session_has_task_group` WRITE;
/*!40000 ALTER TABLE `session_has_task_group` DISABLE KEYS */;
INSERT INTO `session_has_task_group` VALUES (1,1,1),(3,2,2),(4,3,3);
/*!40000 ALTER TABLE `session_has_task_group` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `study` WRITE;
/*!40000 ALTER TABLE `study` DISABLE KEYS */;
INSERT INTO `study` VALUES (1,'Ashwini Shandana test study','This study has the collaboration widgets on','ash01'),(2,'youngjistudy','Desc','yji'),(3,'Min Santha test study','This study has task survey plugin','minsa01');
/* for wack-a-mole-plugin */
INSERT INTO `study` VALUES (4,'Wack-a-mole test study','This study has the wack-a-mole plugin','minsa');
/*!40000 ALTER TABLE `study` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `study_has_research_group` WRITE;
/*!40000 ALTER TABLE `study_has_research_group` DISABLE KEYS */;
INSERT INTO `study_has_research_group` VALUES (1,1,1),(3,3,1),(4,4,1);
/*!40000 ALTER TABLE `study_has_research_group` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (1,'stc01','carlos',1),(2,'stc02','johannes',1),(3,'minsa01','min',2),(4,'minsa02','santha',2);
/* for wack-a-mole plugin */
INSERT INTO `subject` VALUES (5,'wack1','min',3),(6,'wack2','santha',3);
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,'sudoku task ','sudokuPlugin',0,1000,1,'task intro ',10,1,'primer for task',10,0,'Here we gooo','G',0,0,0,'S',NULL,NULL),(2,'surveyTask','surveyPlugin',0,1000,1,'Please answer all survey questions.',10,0,'',0,0,'','G',0,0,0,'S',NULL,NULL);
/* for wack-a-mole plugin */
INSERT INTO `task` VALUES (3,'wack-a-mole task','wackamolePlugin',0,1000,1,'Please answer all survey questions.',10,0,'',0,0,'','G',0,0,0,'S',NULL,NULL);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `task_configuration` WRITE;
/*!40000 ALTER TABLE `task_configuration` DISABLE KEYS */;
INSERT INTO `task_configuration` VALUES (1,'sudokuPlugin9x9','sudokuPlugin'),(2,'surveySimpleQuestions','surveyPlugin');
/* for wack-a-mole plugin */
INSERT INTO `task_configuration` VALUES (3,'simpleWackamole','wackamolePlugin');
/*!40000 ALTER TABLE `task_configuration` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `task_execution_attribute` WRITE;
/*!40000 ALTER TABLE `task_execution_attribute` DISABLE KEYS */;
INSERT INTO `task_execution_attribute` VALUES (1,'inputCell_0_0','2',NULL,NULL,1),(2,'filledCell_0_1','7',NULL,NULL,1),(3,'inputCell_0_2','2',NULL,NULL,1),(4,'inputCell_0_3','8',NULL,NULL,1),(5,'inputCell_0_4','5',NULL,NULL,1),(6,'filledCell_0_5','2',NULL,NULL,1),(7,'filledCell_0_6','5',NULL,NULL,1),(8,'inputCell_0_7','2',NULL,NULL,1),(9,'filledCell_0_8','8',NULL,NULL,1),(10,'inputCell_1_0','5',NULL,NULL,1),(11,'filledCell_1_1','9',NULL,NULL,1),(12,'filledCell_1_2','6',NULL,NULL,1),(13,'inputCell_1_3','4',NULL,NULL,1),(14,'inputCell_1_4','1',NULL,NULL,1),(15,'filledCell_1_5','1',NULL,NULL,1),(16,'inputCell_1_6','3',NULL,NULL,1),(17,'inputCell_1_7','7',NULL,NULL,1),(18,'filledCell_1_8','7',NULL,NULL,1),(19,'inputCell_2_0','3',NULL,NULL,1),(20,'filledCell_2_1','5',NULL,NULL,1),(21,'inputCell_2_2','1',NULL,NULL,1),(22,'filledCell_2_3','9',NULL,NULL,1),(23,'inputCell_2_4','2',NULL,NULL,1),(24,'inputCell_2_5','2',NULL,NULL,1),(25,'inputCell_2_6','2',NULL,NULL,1),(26,'inputCell_2_7','2',NULL,NULL,1),(27,'inputCell_2_8','2',NULL,NULL,1),(28,'gridBluePrint','0,7,0,0,0,2,5,0,8,0,9,6,0,0,1,0,0,7,0,5,0,9,0,0,0,0,0,0,0,0,7,2,0,4,8,3,6,0,0,0,9,0,0,0,5,2,8,3,0,1,5,0,0,0,0,0,0,0,0,8,0,4,0,7,0,0,4,0,0,3,5,0,9,0,5,1,0,0,0,6,2',NULL,NULL,1);
/* for survey plugin */
INSERT INTO `task_execution_attribute` VALUES (29, 'surveyBluePrint','[{"question":"Lorem ipsum dolor sit amet, laudem habemus contentiones his no. Suas volumus ne sit, ferri graecis ne ius, an vix aliquip commune?","type":"text","placeholder":"sample question1 placeholder"},{"question":"Natum ridens dissentiunt eos ne, partem diceret eloquentiam ea ius?","type":"text","placeholder":"sample question2 placeholder","video_url":"https://www.youtube.com/embed/zVXnoIoWu88"},{"type":"introduction","question":"This is the introduction. Following 2 questions are based on this paragraph.  Try the Brain Pickings email newsletter  I know, I know. Pop-ups aren''t classy. But that''s the point: The Internet is a messy and distracting place, and Brain Pickings is contemplative reading. So why not give it proper room for reflection? Every Sunday morning, get the week''s most interesting articles in one distilled digest straight to your inbox. "},{"question":"Eu menandri dissentias sea, et noster epicurei tacimates eum, ut malis denique disputando sit?","type":"text","placeholder":"sample question3 placeholder"},{"question":"radio question #1","type":"radio","value":["choice 1","choice 2","choice 3"],"video_url":"https://www.youtube.com/embed/aJaZc4E8Y4U"},{"question":"radio question #2","type":"radio","value":["option 1","option 2","option 3"]},{"question":"Eu menandri dissentias sea, et noster epicurei tacimates eum, ut malis denique disputando sit?","type":"select","options":["option1","option2","option3"]},{"question":"checkbox question #1","type":"checkbox","value":["option 1","option 2","option 3"]},{"question":"checkbox question #2","type":"checkbox","value":["option 1","option 2","option 3"]}]', NULL, NULL, 2);
/*!40000 ALTER TABLE `task_execution_attribute` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `task_group` WRITE;
/*!40000 ALTER TABLE `task_group` DISABLE KEYS */;
INSERT INTO `task_group` VALUES (1,'sudokutaskgroup'),(2,'surveytaskgroup'), (3, 'wackamoletaskgroup');
/*!40000 ALTER TABLE `task_group` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `task_group_has_task` WRITE;
/*!40000 ALTER TABLE `task_group_has_task` DISABLE KEYS */;
INSERT INTO `task_group_has_task` VALUES (1,0,1,1),(2,0,2,2), (3,0,3,3);
/*!40000 ALTER TABLE `task_group_has_task` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `task_has_research_group` WRITE;
/*!40000 ALTER TABLE `task_has_research_group` DISABLE KEYS */;
INSERT INTO `task_has_research_group` VALUES (1,1,1),(2,1,2),(3,1,3);
/*!40000 ALTER TABLE `task_has_research_group` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `task_has_task_configuration` WRITE;
/*!40000 ALTER TABLE `task_has_task_configuration` DISABLE KEYS */;
INSERT INTO `task_has_task_configuration` VALUES (1,1,1),(2,2,2),(3,3,3);
/*!40000 ALTER TABLE `task_has_task_configuration` ENABLE KEYS */;
UNLOCK TABLES;

SET FOREIGN_KEY_CHECKS=1;