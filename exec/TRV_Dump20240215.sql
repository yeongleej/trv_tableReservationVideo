-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: i10b201.p.ssafy.io    Database: jariyo
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm` (
  `alarm_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `alarm_content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alarm_created_at` datetime(6) DEFAULT NULL,
  `alarm_is_checked` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`alarm_id`),
  KEY `FKd6g1gp6sn8nt3ku8y2mgu41vs` (`user_id`),
  CONSTRAINT `FKd6g1gp6sn8nt3ku8y2mgu41vs` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarm`
--

LOCK TABLES `alarm` WRITE;
/*!40000 ALTER TABLE `alarm` DISABLE KEYS */;
/*!40000 ALTER TABLE `alarm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `board`
--

DROP TABLE IF EXISTS `board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `board` (
  `board_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `board_content` tinytext COLLATE utf8mb4_unicode_ci,
  `board_domain` enum('GENERAL','RECIPE','NOTIFICATION') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `board_title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`board_id`),
  KEY `FKqrcx4shwcq3xlx22i147o9dps` (`store_id`),
  KEY `FKfyf1fchnby6hndhlfaidier1r` (`user_id`),
  CONSTRAINT `FKfyf1fchnby6hndhlfaidier1r` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKqrcx4shwcq3xlx22i147o9dps` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `board`
--

LOCK TABLES `board` WRITE;
/*!40000 ALTER TABLE `board` DISABLE KEYS */;
INSERT INTO `board` VALUES (1,_binary '\0','2024-02-15 04:23:01.046600','2024-02-15 04:23:01.046600','곱창 맛있는 집 있나요?','GENERAL','곱창 맛집 알려주세요!',NULL,1),(2,_binary '\0','2024-02-15 04:23:08.656832','2024-02-15 04:23:08.656832','여기 식당 정말 좋았어요! 추천합니다.','GENERAL','새로운 식당 리뷰',NULL,2),(6,_binary '\0','2024-02-15 05:51:48.162510','2024-02-15 05:51:48.162510','여기 식당 정말 좋았어요! 추천합니다.','RECIPE','왕감자',NULL,2),(7,_binary '\0','2024-02-15 05:52:36.411871','2024-02-15 05:52:36.411871','아아','GENERAL','뻘글',1,2),(11,_binary '\0','2024-02-15 06:28:01.616579','2024-02-15 06:28:01.616579','여기 식당 정말 좋았어요! 추천합니다.','GENERAL','새로운 식당 리뷰',NULL,NULL),(12,_binary '\0','2024-02-15 06:32:34.402791','2024-02-15 06:32:34.402791','내가 누굴까?','GENERAL','익명의 글 작성',NULL,NULL),(13,_binary '\0','2024-02-15 06:33:49.225481','2024-02-15 06:33:49.225481','여기 식당 정말 좋았어요! 추천합니다.','GENERAL','오 여기가 TRV 인가요?',NULL,NULL),(15,_binary '\0','2024-02-15 11:04:04.350300','2024-02-15 11:04:04.350300','사드세요','RECIPE','피자만들기',NULL,NULL);
/*!40000 ALTER TABLE `board` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `boards3image`
--

DROP TABLE IF EXISTS `boards3image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `boards3image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `board_id` bigint DEFAULT NULL,
  `image_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7tkqpwnvdd7hw4ffqf4lf125u` (`image_id`),
  KEY `FKt2borq7j27jbh5ywaaxnmg9x` (`board_id`),
  CONSTRAINT `FK5mpx48cla2yjhwraed14egwkq` FOREIGN KEY (`image_id`) REFERENCES `s3image` (`s3image_id`),
  CONSTRAINT `FKt2borq7j27jbh5ywaaxnmg9x` FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `boards3image`
--

LOCK TABLES `boards3image` WRITE;
/*!40000 ALTER TABLE `boards3image` DISABLE KEYS */;
INSERT INTO `boards3image` VALUES (1,_binary '\0','2024-02-15 04:23:08.668909','2024-02-15 04:23:08.637194',2,1),(2,_binary '\0','2024-02-15 05:52:36.422120','2024-02-15 05:52:35.727644',7,9),(3,_binary '\0','2024-02-15 05:52:36.424123','2024-02-15 05:52:35.801861',7,10),(4,_binary '\0','2024-02-15 05:52:36.424123','2024-02-15 05:52:35.903860',7,11),(5,_binary '\0','2024-02-15 05:52:36.424123','2024-02-15 05:52:36.060945',7,12),(6,_binary '\0','2024-02-15 05:52:36.424123','2024-02-15 05:52:36.173264',7,13),(7,_binary '\0','2024-02-15 05:52:36.424123','2024-02-15 05:52:36.280757',7,14),(8,_binary '\0','2024-02-15 05:52:36.424123','2024-02-15 05:52:36.400847',7,15),(9,_binary '\0','2024-02-15 06:32:34.415890','2024-02-15 06:32:34.389792',12,18);
/*!40000 ALTER TABLE `boards3image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_room`
--

DROP TABLE IF EXISTS `chat_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_room` (
  `chatroom_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `chatroom_end_date` datetime(6) DEFAULT NULL,
  `chatroom_start_date` datetime(6) DEFAULT NULL,
  `chatroom_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chatroom_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chatroom_user_count` int DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  PRIMARY KEY (`chatroom_id`),
  KEY `FKj67oq5jsc3ihket9u26npmnnj` (`store_id`),
  CONSTRAINT `FKj67oq5jsc3ihket9u26npmnnj` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_room`
--

LOCK TABLES `chat_room` WRITE;
/*!40000 ALTER TABLE `chat_room` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dining_table`
--

DROP TABLE IF EXISTS `dining_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dining_table` (
  `dining_table_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `dining_table_capacity` int DEFAULT NULL,
  `dining_table_height` int DEFAULT NULL,
  `dining_table_is_available` bit(1) DEFAULT NULL,
  `dining_table_number` int DEFAULT NULL,
  `dining_table_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dining_table_width` int DEFAULT NULL,
  `dining_table_x` int DEFAULT NULL,
  `dining_table_y` int DEFAULT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`dining_table_id`),
  UNIQUE KEY `STORE_DININGTABLE_NUMBER_UNIQUE` (`store_id`,`dining_table_number`),
  CONSTRAINT `FKol7x6omyqn2qr6go7b3hxxs5c` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dining_table`
--

LOCK TABLES `dining_table` WRITE;
/*!40000 ALTER TABLE `dining_table` DISABLE KEYS */;
INSERT INTO `dining_table` VALUES (1,_binary '\0','2024-02-15 04:34:56.734704','2024-02-15 04:34:56.734704',4,80,_binary '',1,'4인 테이블',80,202,210,1),(2,_binary '\0','2024-02-15 04:34:56.749704','2024-02-15 04:34:56.749704',4,80,_binary '',2,'4인 테이블',80,214,363,1),(3,_binary '\0','2024-02-15 04:34:56.759703','2024-02-15 04:34:56.759703',4,80,_binary '',3,'원형테이블',80,438,242,1),(4,_binary '\0','2024-02-15 04:34:56.769703','2024-02-15 04:34:56.769703',0,30,_binary '\0',4,'창문',80,208,309,1),(8,_binary '\0','2024-02-15 14:03:46.744385','2024-02-15 14:03:46.744385',2,60,_binary '',1,'2인 테이블',60,65,178,5),(9,_binary '\0','2024-02-15 14:03:46.755390','2024-02-15 14:03:46.755390',2,60,_binary '',2,'2인 테이블',60,144,177,5),(10,_binary '\0','2024-02-15 14:03:46.765391','2024-02-15 14:03:46.765391',2,60,_binary '',3,'2인 테이블',60,228,179,5),(11,_binary '\0','2024-02-15 14:03:46.774385','2024-02-15 14:03:46.774385',2,60,_binary '',4,'2인 테이블',60,310,181,5),(12,_binary '\0','2024-02-15 14:03:46.783404','2024-02-15 14:03:46.783404',0,80,_binary '\0',5,'창문',30,0,180,5),(13,_binary '\0','2024-02-15 14:03:46.792384','2024-02-15 14:03:46.792384',0,80,_binary '\0',6,'창문',30,1,354,5),(14,_binary '\0','2024-02-15 14:03:46.805385','2024-02-15 14:03:46.805385',0,80,_binary '\0',7,'창문',30,0,268,5),(15,_binary '\0','2024-02-15 14:03:46.816385','2024-02-15 14:03:46.816385',0,30,_binary '\0',8,'입구',60,521,278,5),(16,_binary '\0','2024-02-15 14:03:46.827387','2024-02-15 14:03:46.827387',0,60,_binary '\0',9,'카운터',60,441,318,5),(17,_binary '\0','2024-02-15 14:03:46.834406','2024-02-15 14:03:46.834406',4,80,_binary '',10,'4인 테이블',80,162,359,5),(18,_binary '\0','2024-02-15 14:03:46.843397','2024-02-15 14:03:46.843397',4,80,_binary '',11,'4인 테이블',80,158,266,5),(19,_binary '\0','2024-02-15 14:03:46.853401','2024-02-15 14:03:46.853401',4,80,_binary '',12,'4인 테이블',80,72,358,5),(20,_binary '\0','2024-02-15 14:03:46.862384','2024-02-15 14:03:46.862384',4,80,_binary '',13,'4인 테이블',80,69,266,5),(21,_binary '\0','2024-02-15 14:03:46.874387','2024-02-15 14:03:46.874387',4,60,_binary '',14,'룸',60,264,379,5),(22,_binary '\0','2024-02-15 14:03:46.883385','2024-02-15 14:03:46.883385',4,60,_binary '',15,'룸',60,344,379,5),(23,_binary '\0','2024-02-15 14:03:46.893392','2024-02-15 14:03:46.893392',0,30,_binary '\0',16,'창문',80,407,181,5),(24,_binary '\0','2024-02-15 14:03:46.902385','2024-02-15 14:03:46.902385',0,80,_binary '\0',17,'창문',30,0,180,5),(25,_binary '\0','2024-02-15 14:03:46.912386','2024-02-15 14:03:46.912386',0,80,_binary '\0',18,'창문',30,516,181,5),(64,_binary '\0','2024-02-15 14:51:38.800797','2024-02-15 14:51:38.800797',2,60,_binary '',1,'2인 테이블',60,288,252,3),(65,_binary '\0','2024-02-15 14:51:38.833618','2024-02-15 14:51:38.833618',4,80,_binary '',2,'4인 테이블',80,440,250,3),(66,_binary '\0','2024-02-15 14:51:38.845114','2024-02-15 14:51:38.845114',1,40,_binary '',3,'1인 테이블',40,169,327,3),(67,_binary '\0','2024-02-15 14:51:38.854092','2024-02-15 14:51:38.854092',1,40,_binary '',4,'1인 테이블',40,171,271,3),(68,_binary '\0','2024-02-15 14:51:38.863064','2024-02-15 14:51:38.863064',0,60,_binary '\0',5,'카운터',60,169,402,3),(69,_binary '\0','2024-02-15 14:51:38.872081','2024-02-15 14:51:38.872081',0,30,_binary '\0',6,'창문',80,486,200,3),(70,_binary '\0','2024-02-15 14:51:38.884534','2024-02-15 14:51:38.884534',0,80,_binary '\0',7,'창문',30,540,250,3),(71,_binary '\0','2024-02-15 14:51:38.893966','2024-02-15 14:51:38.893966',0,30,_binary '\0',8,'창문',80,392,199,3),(72,_binary '\0','2024-02-15 14:51:38.903746','2024-02-15 14:51:38.903746',0,30,_binary '\0',9,'입구',60,374,482,3),(73,_binary '\0','2024-02-15 14:51:38.913719','2024-02-15 14:51:38.913719',0,30,_binary '\0',10,'입구',60,305,484,3),(74,_binary '\0','2024-02-15 14:51:38.922086','2024-02-15 14:51:38.922086',2,60,_binary '',11,'2인 테이블',60,292,328,3),(75,_binary '\0','2024-02-15 14:51:38.931747','2024-02-15 14:51:38.931747',1,40,_binary '',12,'1인 테이블',40,169,214,3),(76,_binary '\0','2024-02-15 14:51:38.940502','2024-02-15 14:51:38.940502',4,80,_binary '',13,'4인 테이블',80,0,212,3),(77,_binary '\0','2024-02-15 14:51:38.949592','2024-02-15 14:51:38.949592',1,40,_binary '',14,'1인 테이블',40,0,142,3),(78,_binary '\0','2024-02-15 14:51:38.957549','2024-02-15 14:51:38.957549',1,40,_binary '',15,'1인 테이블',40,643,144,3),(79,_binary '\0','2024-02-15 12:38:54.959061','2024-02-15 12:38:54.959061',2,60,_binary '',1,'2인 테이블',60,233,205,8),(80,_binary '\0','2024-02-15 12:38:54.964373','2024-02-15 12:38:54.964373',0,30,_binary '\0',3,'창문',80,278,157,8),(81,_binary '\0','2024-02-15 13:24:15.445474','2024-02-15 13:24:15.445474',4,80,_binary '',1,'4인 테이블',80,115,211,4),(82,_binary '\0','2024-02-15 13:24:15.456789','2024-02-15 13:24:15.456789',4,80,_binary '',2,'4인 테이블',80,220,211,4),(83,_binary '\0','2024-02-15 13:24:15.465854','2024-02-15 13:24:15.465854',4,80,_binary '',3,'4인 테이블',80,327,211,4),(84,_binary '\0','2024-02-15 13:24:15.473179','2024-02-15 13:24:15.473179',4,80,_binary '',4,'4인 테이블',80,436,209,4),(85,_binary '\0','2024-02-15 13:24:15.480689','2024-02-15 13:24:15.480689',1,40,_binary '',5,'1인 테이블',40,87,374,4),(86,_binary '\0','2024-02-15 13:24:15.489504','2024-02-15 13:24:15.489504',1,40,_binary '',6,'1인 테이블',40,86,448,4),(87,_binary '\0','2024-02-15 13:24:15.497336','2024-02-15 13:24:15.497336',1,40,_binary '',7,'1인 테이블',40,86,518,4),(88,_binary '\0','2024-02-15 13:24:15.505341','2024-02-15 13:24:15.505341',1,40,_binary '',8,'1인 테이블',40,87,593,4),(89,_binary '\0','2024-02-15 13:24:15.513677','2024-02-15 13:24:15.513677',0,80,_binary '\0',9,'창문',30,0,350,4),(90,_binary '\0','2024-02-15 13:24:15.522841','2024-02-15 13:24:15.522841',0,80,_binary '\0',10,'창문',30,0,207,4),(91,_binary '\0','2024-02-15 13:24:15.529864','2024-02-15 13:24:15.529864',0,80,_binary '\0',11,'창문',30,0,475,4),(92,_binary '\0','2024-02-15 13:24:15.538300','2024-02-15 13:24:15.538300',0,80,_binary '\0',12,'창문',30,0,588,4),(93,_binary '\0','2024-02-15 13:24:15.546649','2024-02-15 13:24:15.546649',4,80,_binary '',13,'원형테이블',80,229,362,4),(94,_binary '\0','2024-02-15 13:24:15.553660','2024-02-15 13:24:15.553660',2,60,_binary '',14,'2인 테이블',60,390,370,4),(95,_binary '\0','2024-02-15 13:24:15.561967','2024-02-15 13:24:15.561967',4,80,_binary '',15,'원형테이블',80,386,493,4),(96,_binary '\0','2024-02-15 13:24:15.570573','2024-02-15 13:24:15.570573',2,60,_binary '',16,'2인 테이블',60,241,506,4),(97,_binary '\0','2024-02-15 13:24:15.580574','2024-02-15 13:24:15.580574',0,30,_binary '\0',17,'입구',60,507,681,4),(98,_binary '\0','2024-02-15 13:24:15.589899','2024-02-15 13:24:15.589899',8,80,_binary '',18,'단체석',160,509,354,4),(99,_binary '\0','2024-02-15 13:24:15.598116','2024-02-15 13:24:15.598116',0,60,_binary '\0',19,'카운터',60,435,652,4);
/*!40000 ALTER TABLE `dining_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `follow_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `store_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`follow_id`),
  KEY `FK994r8tobnh44rvgk6xr8qrn1v` (`store_id`),
  KEY `FKjby6aprc2rh3sxi3qu46jb22q` (`user_id`),
  CONSTRAINT `FK994r8tobnh44rvgk6xr8qrn1v` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
  CONSTRAINT `FKjby6aprc2rh3sxi3qu46jb22q` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
INSERT INTO `follow` VALUES (1,_binary '','2024-02-15 13:40:43.611668','2024-02-15 13:32:26.069781',1,3),(2,_binary '\0','2024-02-15 04:41:29.466771','2024-02-15 04:41:29.466771',4,1),(3,_binary '\0','2024-02-15 04:43:05.666833','2024-02-15 04:43:05.666833',1,1),(7,_binary '\0','2024-02-15 05:05:42.512722','2024-02-15 05:05:42.512722',5,1),(12,_binary '\0','2024-02-15 05:09:14.991236','2024-02-15 05:09:14.991236',3,1),(14,_binary '\0','2024-02-15 14:59:22.742012','2024-02-15 14:59:22.742012',3,3),(15,_binary '\0','2024-02-15 15:27:42.513698','2024-02-15 15:27:42.513698',1,6),(16,_binary '\0','2024-02-15 07:07:31.260020','2024-02-15 07:07:31.260020',5,4),(17,_binary '\0','2024-02-15 07:19:38.089606','2024-02-15 07:19:38.089606',4,6),(18,_binary '','2024-02-15 12:53:10.680572','2024-02-15 12:44:46.282125',1,9),(19,_binary '\0','2024-02-15 13:00:09.602887','2024-02-15 13:00:09.602887',4,9),(20,_binary '\0','2024-02-15 22:01:55.684876','2024-02-15 22:01:55.684876',4,3),(21,_binary '','2024-02-15 13:32:22.383653','2024-02-15 13:11:23.289347',4,8),(22,_binary '\0','2024-02-15 13:26:54.098026','2024-02-15 13:18:51.812043',4,10);
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `menu_content` tinytext COLLATE utf8mb4_unicode_ci,
  `menu_images` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `UK_97f2wnhtexr44csf10mkw38p5` (`store_id`),
  CONSTRAINT `FK4sgenfcmk1jajhgctnkpn5erg` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playroom`
--

DROP TABLE IF EXISTS `playroom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playroom` (
  `playroom_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `broadcasting` bit(1) DEFAULT NULL,
  `calling` bit(1) DEFAULT NULL,
  `category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chatting` bit(1) DEFAULT NULL,
  `playroom_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `playroom_info` text COLLATE utf8mb4_unicode_ci,
  `playroom_title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `playroom_user_count` int DEFAULT NULL,
  `waiting` bit(1) DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  PRIMARY KEY (`playroom_id`),
  UNIQUE KEY `UK_9nx1cigthpmooa5topeq5qq0b` (`store_id`),
  CONSTRAINT `FKjbf2lr94sv7tq3jaigxrekv5q` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playroom`
--

LOCK TABLES `playroom` WRITE;
/*!40000 ALTER TABLE `playroom` DISABLE KEYS */;
INSERT INTO `playroom` VALUES (1,_binary '\0','2024-02-15 12:50:32.738510','2024-02-15 04:26:05.697881',_binary '\0',_binary '\0','',_binary '\0','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/uploadTumbnail/20240215/02aeeac8-26d0-4fe0-ad79-51eb23dfa1d3','info','',0,_binary '\0',1),(3,_binary '\0','2024-02-15 13:45:34.738086','2024-02-15 13:28:29.253798',_binary '\0',_binary '\0','',_binary '\0','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/uploadTumbnail/20240215/5dcb4540-bacb-4868-b64d-ce52a6bbd3f6',NULL,'',0,_binary '\0',3),(4,_binary '\0','2024-02-15 13:33:23.149959','2024-02-15 04:37:44.827749',_binary '\0',_binary '\0','',_binary '\0','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/uploadTumbnail/20240215/309935cf-b51c-4c49-8e3d-1c8573511a9e','info','',2,_binary '\0',4),(5,_binary '\0','2024-02-15 06:49:12.861515','2024-02-15 13:59:15.835314',_binary '\0',_binary '','',_binary '',NULL,'info','채팅 전화 웨이팅 ',1,_binary '',5),(8,_binary '\0','2024-02-15 12:31:20.213048','2024-02-15 12:31:20.213048',_binary '\0',_binary '\0','justChat',_binary '\0',NULL,'info','title',0,_binary '\0',8);
/*!40000 ALTER TABLE `playroom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qna_room`
--

DROP TABLE IF EXISTS `qna_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qna_room` (
  `qnaroom_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `qnaroom_end_time` datetime(6) DEFAULT NULL,
  `qnaroom_start_time` datetime(6) DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`qnaroom_id`),
  KEY `FK5dyggiltf4ck81wkbrwnwkysg` (`store_id`),
  KEY `FK6is75ggc4tk91rro620mnvwf3` (`user_id`),
  CONSTRAINT `FK5dyggiltf4ck81wkbrwnwkysg` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
  CONSTRAINT `FK6is75ggc4tk91rro620mnvwf3` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qna_room`
--

LOCK TABLES `qna_room` WRITE;
/*!40000 ALTER TABLE `qna_room` DISABLE KEYS */;
/*!40000 ALTER TABLE `qna_room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `reservation_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `reservation_date` date DEFAULT NULL,
  `reservation_time` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reservation_user_count` int DEFAULT NULL,
  `reservation_status` enum('ACTIVE','INACTIVE','CANCEL') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dining_table_id` bigint NOT NULL,
  `store_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`reservation_id`),
  KEY `FKfo2379cgmxbc02oa50am5jrps` (`dining_table_id`),
  KEY `FKi3wg5pq3qkuxx7e6csndvce63` (`store_id`),
  KEY `FKm4oimk0l1757o9pwavorj6ljg` (`user_id`),
  CONSTRAINT `FKfo2379cgmxbc02oa50am5jrps` FOREIGN KEY (`dining_table_id`) REFERENCES `dining_table` (`dining_table_id`),
  CONSTRAINT `FKi3wg5pq3qkuxx7e6csndvce63` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
  CONSTRAINT `FKm4oimk0l1757o9pwavorj6ljg` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (4,_binary '\0','2024-02-15 14:17:51.261892','2024-02-15 14:17:51.261892','2024-02-16','11:00',2,'ACTIVE',8,5,3),(5,_binary '\0','2024-02-15 15:34:55.161320','2024-02-15 14:17:59.075038','2024-02-16','13:00',2,'CANCEL',19,5,6),(6,_binary '\0','2024-02-15 15:37:08.521758','2024-02-15 14:25:06.206548','2024-02-16','03:00',2,'CANCEL',2,1,6),(7,_binary '\0','2024-02-15 15:37:09.191927','2024-02-15 14:27:04.115204','2024-02-16','00:00',2,'CANCEL',2,1,6),(9,_binary '\0','2024-02-15 14:57:33.565456','2024-02-15 14:57:33.565456','2024-02-15','10:00',1,'ACTIVE',77,3,3),(12,_binary '\0','2024-02-15 22:32:01.573030','2024-02-15 15:58:21.007115','2024-02-22','02:00',2,'CANCEL',2,1,6),(13,_binary '\0','2024-02-15 22:32:21.737553','2024-02-15 15:59:19.101866','2024-02-18','17:00',2,'CANCEL',19,5,6),(14,_binary '\0','2024-02-15 07:08:44.743556','2024-02-15 07:08:44.743556','2024-02-16','12:00',2,'ACTIVE',8,5,4),(15,_binary '\0','2024-02-15 07:09:16.095212','2024-02-15 07:09:16.095212','2024-02-22','17:00',4,'ACTIVE',18,5,4),(16,_binary '\0','2024-02-15 07:50:59.953469','2024-02-15 07:50:59.953469','2024-02-16','13:00',2,'ACTIVE',11,5,2),(17,_binary '\0','2024-02-15 22:32:02.641262','2024-02-15 17:28:17.184154','2024-02-16','12:00',2,'CANCEL',17,5,6),(18,_binary '\0','2024-02-15 17:32:24.224623','2024-02-15 17:31:54.789407','2024-02-15','03:00',2,'CANCEL',3,1,8),(19,_binary '\0','2024-02-15 12:42:28.511018','2024-02-15 12:40:54.689434','2024-02-21','01:00',4,'CANCEL',1,1,9),(20,_binary '\0','2024-02-15 13:04:39.041811','2024-02-15 12:43:49.792910','2024-02-17','13:00',3,'CANCEL',1,1,9),(21,_binary '\0','2024-02-15 13:28:27.685921','2024-02-15 13:27:46.445012','2024-02-15','04:00',1,'CANCEL',88,4,10);
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `review_content` tinytext COLLATE utf8mb4_unicode_ci,
  `review_likes` int DEFAULT NULL,
  `review_star` int DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`review_id`),
  KEY `FK74d12ba8sxxu9vpnc59b43y30` (`store_id`),
  KEY `FKiyf57dy48lyiftdrf7y87rnxi` (`user_id`),
  CONSTRAINT `FK74d12ba8sxxu9vpnc59b43y30` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
  CONSTRAINT `FKiyf57dy48lyiftdrf7y87rnxi` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews3image`
--

DROP TABLE IF EXISTS `reviews3image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews3image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `board_id` bigint DEFAULT NULL,
  `image_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kjn7gpbqppxe0kmqc9yfbt6qd` (`image_id`),
  KEY `FKjo6b2d1rk2cw3gqh7eu0ngk5x` (`board_id`),
  CONSTRAINT `FK1xlifeo23uh5dcwkaaw7g9xeq` FOREIGN KEY (`image_id`) REFERENCES `s3image` (`s3image_id`),
  CONSTRAINT `FKjo6b2d1rk2cw3gqh7eu0ngk5x` FOREIGN KEY (`board_id`) REFERENCES `review` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews3image`
--

LOCK TABLES `reviews3image` WRITE;
/*!40000 ALTER TABLE `reviews3image` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews3image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `s3image`
--

DROP TABLE IF EXISTS `s3image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `s3image` (
  `s3image_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`s3image_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `s3image`
--

LOCK TABLES `s3image` WRITE;
/*!40000 ALTER TABLE `s3image` DISABLE KEYS */;
INSERT INTO `s3image` VALUES (1,_binary '\0','2024-02-15 04:23:08.625235','2024-02-15 04:23:08.625235','2252bba0-d379-4a26-8c4e-02c6affdf351.jpg','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/8b1561b3-8fab-492f-8fe8-1270a919d9d7.jpg'),(2,_binary '\0','2024-02-15 04:26:05.668452','2024-02-15 04:26:05.668452','2252bba0-d379-4a26-8c4e-02c6affdf351.jpg','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/e5ac6544-20c3-4ec6-861b-d5980a18b8b2.jpg'),(3,_binary '\0','2024-02-15 04:26:58.562560','2024-02-15 04:26:58.562560','서명.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/108b0fe5-e3a8-40f5-aa06-291558d8588e.png'),(4,_binary '\0','2024-02-15 13:28:29.207894','2024-02-15 13:28:29.207894','mainlogo3.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/a488d2ba-0f40-4784-bd6f-560d63c56a08.png'),(5,_binary '\0','2024-02-15 04:37:44.794231','2024-02-15 04:37:44.794231','download.jpg','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/6594f9f8-6076-46b9-aca5-88c3f4a06384.jpg'),(6,_binary '\0','2024-02-15 13:59:15.792298','2024-02-15 13:59:15.792298','12.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/979d7125-dc3b-4cf3-861a-90f06b9148c9.png'),(7,_binary '\0','2024-02-15 05:04:13.451825','2024-02-15 05:04:13.451825','등록.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/f7b5df05-ee29-479c-a8e2-2791f988a633.png'),(8,_binary '\0','2024-02-15 14:50:28.896176','2024-02-15 14:50:28.896176','등록.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/c99a205b-0cde-460f-a21e-2ae3cea502f0.png'),(9,_binary '\0','2024-02-15 05:52:35.717274','2024-02-15 05:52:35.717274','d6ad7763-3fac-4a05-a44b-b4d033aae797.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/6d033b13-085e-4813-95ac-8d35df02446a.png'),(10,_binary '\0','2024-02-15 05:52:35.790863','2024-02-15 05:52:35.790863','null.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/bd6bfec8-16ea-45b0-9c0a-89362d82a49c.png'),(11,_binary '\0','2024-02-15 05:52:35.893861','2024-02-15 05:52:35.893861','mainlogo.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/5c79d2ef-cd5e-4e7e-9b40-6af2c4efe004.png'),(12,_binary '\0','2024-02-15 05:52:36.051945','2024-02-15 05:52:36.051945','2252bba0-d379-4a26-8c4e-02c6affdf351.jpg','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/06130c9b-1a81-4fb3-8b8d-f07114f02ebb.jpg'),(13,_binary '\0','2024-02-15 05:52:36.163264','2024-02-15 05:52:36.163264','4fa69c1b-b63b-4e12-b376-07bd67085d1a.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/67d787fa-547f-4816-8768-ed4cb512a90a.png'),(14,_binary '\0','2024-02-15 05:52:36.271758','2024-02-15 05:52:36.271758','f2522114-d623-4fbe-b92f-9ab926f8107e.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/efef1fc5-f8b0-4832-8c9f-775c21f800b2.png'),(15,_binary '\0','2024-02-15 05:52:36.392843','2024-02-15 05:52:36.392843','fed841bd-6ad4-4bf7-9bdc-8a548c8b45d7.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/55051e7a-c01e-4514-8921-4621f5d55160.png'),(16,_binary '\0','2024-02-15 15:07:38.532246','2024-02-15 15:07:38.532246','공차푸바옹.jpg','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/8ff32cc6-d3e7-4797-a69a-713c60926678.jpg'),(17,_binary '\0','2024-02-15 15:14:09.039085','2024-02-15 15:14:09.039085','등록.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/6a133087-14cd-4d89-b17a-da6cecc80163.png'),(18,_binary '\0','2024-02-15 06:32:34.377385','2024-02-15 06:32:34.377385','다운로드.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/47c49e28-0504-4931-b110-eb856173e816.png'),(19,_binary '\0','2024-02-15 16:59:42.838204','2024-02-15 16:59:42.838204','등록.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/58eedc46-99bc-4ec6-88a2-7b2361cc2605.png'),(20,_binary '\0','2024-02-15 17:00:17.597746','2024-02-15 17:00:17.597746','등록.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/898acbb1-ef2f-40ff-8820-c95a3c04f20e.png'),(21,_binary '\0','2024-02-15 12:14:25.368138','2024-02-15 12:14:25.368138','image2.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/d8c05b4c-c573-4a46-ab2a-1f9ec5d2673f.png'),(22,_binary '\0','2024-02-15 12:31:20.206389','2024-02-15 12:31:20.206389','image2.png','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/20240215/c7cd32e7-15d9-4723-b3de-ad456822160d.png');
/*!40000 ALTER TABLE `s3image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store` (
  `store_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `store_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_business_file` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_business_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_detail` tinytext COLLATE utf8mb4_unicode_ci,
  `store_follow_count` int DEFAULT NULL,
  `store_is_wating` bit(1) DEFAULT NULL,
  `store_menu` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_menu_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_operation_dates` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_operation_hours` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_position_x` double DEFAULT NULL,
  `store_position_y` double DEFAULT NULL,
  `store_rating` double DEFAULT NULL,
  `store_reservation_available_dates` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_reservation_available_hours` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `store_reservation_status` enum('ACTIVE','INACTIVE','CANCEL') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`store_id`),
  UNIQUE KEY `UK_5f8d716raarxegq1186hejis9` (`store_business_number`),
  UNIQUE KEY `UK_bjqth9sjo3phwbmybuysm65be` (`user_id`),
  CONSTRAINT `FKn82wpcqrb21yddap4s3ttwnxj` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES (1,_binary '\0','2024-02-15 12:53:10.680359','2024-02-15 04:26:05.687643','서울 마포구 망원동 482-8','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/e68121e9-9df0-44be-baf2-1555121a7582.docx','5035615595',NULL,2,_binary '\0',NULL,'https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/e681a501-5565-44a1-b5c4-645b98d6ac65.jpg','대성동 왕감자',NULL,NULL,NULL,0,0,NULL,'2024-02-14 2024-02-27 2024-02-15 2024-02-16 2024-02-23 2024-02-22 2024-02-21 2024-02-20 2024-02-19 2024-02-26 2024-02-28 2024-02-25 2024-02-18 2024-02-17','00:00 01:00 02:00 03:00 04:00 05:00 11:00 10:00 09:00 08:00 07:00 06:00 12:00 13:00 14:00 15:00 16:00 17:00 23:00 22:00 21:00','ACTIVE',2),(3,_binary '\0','2024-02-15 15:07:38.555541','2024-02-15 13:28:29.239746','서울 종로구 공평동 100','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/e41f0596-308e-46a2-89d2-8c8bb904047c.jpg','2148884534',NULL,2,_binary '\0',NULL,'https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/85efdbe2-e19f-4014-91dc-dd6ad4e9b6c4.png','공차코리아',NULL,NULL,NULL,126.98241823608815,37.571099146545585,NULL,'2024-02-14 2024-02-15 2024-02-16 2024-02-19 2024-02-20 2024-02-21 2024-02-22 2024-02-17','10:00 11:00 12:00','ACTIVE',3),(4,_binary '\0','2024-02-15 13:32:22.383473','2024-02-15 04:37:44.815744','경남 김해시 대청동 산 63-2','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/c8efeffc-bbf4-4944-964c-f376c5f7cf87.png','1043051531',NULL,5,_binary '\0',NULL,'https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/4081151e-dd60-4e36-acbc-382463280ab0.png','Teddy\'s Kitchen',NULL,NULL,NULL,127.302102556765,36.3587935628474,NULL,'2024-02-15 2024-02-21 2024-02-20 2024-02-19 2024-02-18 2024-02-22 2024-02-14','02:00 03:00 04:00','ACTIVE',4),(5,_binary '\0','2024-02-15 13:15:06.163537','2024-02-15 13:59:15.817287','세종특별자치시 어진동 539','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/72319649-885f-4cfa-903b-863be3f69914.png','1557600284','영업안해',2,_binary '\0','ㄴㅇㄴㅁㄷㅁㄴㄷㅁㄴㄷㅁㄷㄴㅁ','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/0b06a84b-0584-4440-9080-92caa17a5059.PNG','ALO세종점','안해','없어','없음',0,0,NULL,'2024-02-13 2024-02-20 2024-02-21 2024-02-15 2024-02-22 2024-02-16 2024-02-23 2024-02-24 2024-02-25 2024-02-17 2024-02-18 2024-02-19 2024-02-26 2024-02-27 2024-02-28','12:00 13:00 17:00 19:00 18:00 14:00','ACTIVE',6),(8,_binary '\0','2024-02-15 12:38:23.235239','2024-02-15 12:31:20.210673','서울 마포구 대흥동 660','https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/1927dd64-3f78-4811-b55f-bc0f11d04cdb.png','4481400970',NULL,0,_binary '\0',NULL,'https://jariyo-s3.s3.ap-northeast-2.amazonaws.com/employ/20240215/afefc44c-bb46-40a1-897c-229e0384ec1a.png','혜령컴퍼니',NULL,NULL,NULL,126.94257048117983,37.54473232045174,NULL,'2024-02-18 2024-02-19 2024-02-22 2024-02-23','12:00 13:00 16:00 17:00','ACTIVE',1);
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_category`
--

DROP TABLE IF EXISTS `store_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_category` (
  `store_category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  PRIMARY KEY (`store_category_id`),
  KEY `FKm2p2repecp4mx2i2ibmw75deb` (`category_id`),
  KEY `FK3xftrk8cr9na0kj9c21kirtwt` (`store_id`),
  CONSTRAINT `FK3xftrk8cr9na0kj9c21kirtwt` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
  CONSTRAINT `FKm2p2repecp4mx2i2ibmw75deb` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_category`
--

LOCK TABLES `store_category` WRITE;
/*!40000 ALTER TABLE `store_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stores3image`
--

DROP TABLE IF EXISTS `stores3image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stores3image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `image_id` bigint DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  `board_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7qnjvwg8o478vq2hgmg4gjjrr` (`image_id`),
  KEY `FKc07ln2vc48pqp5begp4kq4fyx` (`store_id`),
  KEY `FKi430cpopgkpc8wd64etcqb67t` (`board_id`),
  CONSTRAINT `FK4v3gf3xw57sowrco83hxh39d8` FOREIGN KEY (`image_id`) REFERENCES `s3image` (`s3image_id`),
  CONSTRAINT `FKc07ln2vc48pqp5begp4kq4fyx` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`),
  CONSTRAINT `FKi430cpopgkpc8wd64etcqb67t` FOREIGN KEY (`board_id`) REFERENCES `store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stores3image`
--

LOCK TABLES `stores3image` WRITE;
/*!40000 ALTER TABLE `stores3image` DISABLE KEYS */;
INSERT INTO `stores3image` VALUES (1,_binary '\0','2024-02-15 04:26:05.736744','2024-02-15 04:26:05.677577',2,1,NULL),(3,_binary '\0','2024-02-15 13:28:29.314231','2024-02-15 13:28:29.222793',4,NULL,3),(4,_binary '\0','2024-02-15 04:37:44.864268','2024-02-15 04:37:44.805235',5,4,NULL),(5,_binary '\0','2024-02-15 13:59:15.901319','2024-02-15 13:59:15.804285',6,5,NULL),(8,_binary '\0','2024-02-15 15:07:38.543429','2024-02-15 15:07:38.543429',16,3,NULL),(13,_binary '\0','2024-02-15 12:31:20.221581','2024-02-15 12:31:20.208732',22,8,NULL);
/*!40000 ALTER TABLE `stores3image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `hometown` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nickname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ADMIN','OWNER','USER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `social_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `social_type` enum('KAKAO','NAVER','GOOGLE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('ACTIVE','INACTIVE','CANCEL') COLLATE utf8mb4_unicode_ci NOT NULL,
  `tid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,_binary '\0','2024-02-15 12:31:20.221442','2024-02-15 04:21:51.932591','gpfudgpdus@naver.com',NULL,'http://k.kakaocdn.net/dn/UwTsG/btsyUfpJAi1/mYVMEveQZvPZ1VHK4meKCk/img_110x110.jpg',NULL,'혜령',NULL,'OWNER','3331785985','KAKAO','ACTIVE','T5cdb2e71c025b0ff582'),(2,_binary '\0','2024-02-15 04:26:05.736744','2024-02-15 04:22:18.920889','chomu0821@gmail.com',NULL,'http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg',NULL,'남찬현',NULL,'OWNER','3330236288','KAKAO','ACTIVE',NULL),(3,_binary '\0','2024-02-15 15:07:38.553515','2024-02-15 13:22:51.085422','ljy23589534@nate.com','대전','http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg','이지영','이지영',NULL,'OWNER','3332796139','KAKAO','ACTIVE','T5cd999e1c025b0ff466'),(4,_binary '\0','2024-02-15 04:37:44.864268','2024-02-15 04:23:29.857078','apples5060@kakao.com',NULL,'http://k.kakaocdn.net/dn/DJzRv/btsA0aHofZd/HFeGCKyB0dvP9mhK13kwOK/img_110x110.jpg',NULL,'김태희',NULL,'OWNER','3333111074','KAKAO','ACTIVE',NULL),(6,_binary '\0','2024-02-15 15:26:07.320113','2024-02-15 13:34:14.091910','kjh9522@gmail.com','대전','http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg',NULL,'강준혁','01012345567','OWNER','3330126416','KAKAO','ACTIVE',NULL),(8,_binary '\0','2024-02-15 21:21:47.325865','2024-02-15 17:12:02.305984','hy8463@naver.com',NULL,'http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg',NULL,'최호연',NULL,'USER','3330107217','KAKAO','ACTIVE','T5ce01db1c025b0ff8d1'),(9,_binary '\0','2024-02-15 12:25:21.878768','2024-02-15 12:25:21.878768','gpfudgpdus@jr.naver.com',NULL,'https://ssl.pstatic.net/static/pwe/address/img_profile.png',NULL,'임혜령',NULL,'USER','GSwfeMp4M1qHmnIpDYzHxyRU2M7_E1jDMlDBcNrQbPk','NAVER','ACTIVE',NULL),(10,_binary '\0','2024-02-15 13:30:30.453732','2024-02-15 13:07:20.445887','spdlqj9522@naver.com','서울','https://ssl.pstatic.net/static/pwe/address/img_profile.png',NULL,'강준혁','01026309522','USER','9-1zqgmD-f0eT-Y8NErJcAMTwwCut7shhiEE-wyV-y4','NAVER','ACTIVE','T5ce11f6196d5d2066b6');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_join_list`
--

DROP TABLE IF EXISTS `user_join_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_join_list` (
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `playroom_id` bigint DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `user_join_list_id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`user_join_list_id`),
  KEY `FKh18d0s9rdnkkvrv027bifd7ih` (`playroom_id`),
  KEY `FK603v0a9jc2wom8ep7hjj16t8s` (`user_id`),
  CONSTRAINT `FK603v0a9jc2wom8ep7hjj16t8s` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKh18d0s9rdnkkvrv027bifd7ih` FOREIGN KEY (`playroom_id`) REFERENCES `playroom` (`playroom_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_join_list`
--

LOCK TABLES `user_join_list` WRITE;
/*!40000 ALTER TABLE `user_join_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_join_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zpass`
--

DROP TABLE IF EXISTS `zpass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zpass` (
  `is_deleted` bit(1) NOT NULL,
  `mod_date` datetime(6) DEFAULT NULL,
  `reg_date` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `zpass_id` bigint NOT NULL AUTO_INCREMENT,
  `zpass_created_dates` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zpass_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zpass_status` enum('ACTIVE','INACTIVE','CANCEL') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`zpass_id`),
  KEY `FKb0ot6ubh8jw1un787rhja7g3j` (`user_id`),
  CONSTRAINT `FKb0ot6ubh8jw1un787rhja7g3j` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zpass`
--

LOCK TABLES `zpass` WRITE;
/*!40000 ALTER TABLE `zpass` DISABLE KEYS */;
INSERT INTO `zpass` VALUES (_binary '\0','2024-02-15 14:17:54.793712','2024-02-15 13:57:20.458677',3,1,'2024-02-15T13:57:02','이지영','INACTIVE'),(_binary '\0','2024-02-15 21:22:13.918902','2024-02-15 21:22:13.918902',8,11,'2024-02-15T21:21:48','최호연','ACTIVE');
/*!40000 ALTER TABLE `zpass` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-02-15 22:42:44
