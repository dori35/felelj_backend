
DROP DATABASE IF EXISTS `feleljdb`;
CREATE DATABASE `feleljdb`;
USE `feleljdb`;

DROP TABLE IF EXISTS `answer`;
DROP TABLE IF EXISTS `test_fill`;
DROP TABLE IF EXISTS `choice`;
DROP TABLE IF EXISTS `task`;
DROP TABLE IF EXISTS `test`;
DROP TABLE IF EXISTS `user`;


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hcenuj4ofutyg7lgx0dh017fh` (`identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `random` bit(1) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `created_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h5gvk6ajgccrf6p06c6s13q6j` (`url`),
  KEY `FKndli15cx5ygsvum345miroxe6` (`created_by_id`),
  CONSTRAINT `FKndli15cx5ygsvum345miroxe6` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;



/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `point` int DEFAULT NULL,
  `solution` varchar(255) DEFAULT NULL,
  `task_type` int DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `time_frame` int DEFAULT NULL,
  `test_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKojhuohbslrwalcmrql2wvad8m` (`test_id`),
  CONSTRAINT `FKojhuohbslrwalcmrql2wvad8m` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;



/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `choice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `task_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqc5aro46b189hud31k1kamdaj` (`task_id`),
  CONSTRAINT `FKqc5aro46b189hud31k1kamdaj` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_fill` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fill_date` datetime(6) DEFAULT NULL,
  `point` int DEFAULT NULL,
  `start_date` varchar(255) DEFAULT NULL,
  `test_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKghvmmu9bbj1vrr0ibd75par9u` (`test_id`),
  KEY `FKd5dv68derh6j7q9bfqgqnyexg` (`user_id`),
  CONSTRAINT `FKd5dv68derh6j7q9bfqgqnyexg` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKghvmmu9bbj1vrr0ibd75par9u` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `answer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `answer` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `task_id` bigint DEFAULT NULL,
  `test_fill_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32s29gitnrc8dhk0m2qf218r3` (`task_id`),
  KEY `FK6r579x84i6pbhgdqjop6t4yhd` (`test_fill_id`),
  CONSTRAINT `FK32s29gitnrc8dhk0m2qf218r3` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
  CONSTRAINT `FK6r579x84i6pbhgdqjop6t4yhd` FOREIGN KEY (`test_fill_id`) REFERENCES `test_fill` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


