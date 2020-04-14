-- MariaDB dump 10.17  Distrib 10.4.12-MariaDB, for Linux (x86_64)
--
-- Host: eu-cdbr-west-02.cleardb.net    Database: heroku_fbd6ce354f06502
-- ------------------------------------------------------
-- Server version	5.6.47-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `account_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` int(10) unsigned NOT NULL,
  `deposited_amount` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (111,41,1000000),(121,41,500000),(131,51,5000000),(141,61,2500000),(151,71,7500000),(161,71,2500000);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `account_total_amount`
--

DROP TABLE IF EXISTS `account_total_amount`;
/*!50001 DROP VIEW IF EXISTS `account_total_amount`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `account_total_amount` (
  `account_id` tinyint NOT NULL,
  `total_amount` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `customer_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `full_name` varchar(60) NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `password_salt` char(30) NOT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (41,'Gregory Melendez','gregory_melendez','hE4Z5phPsV/wwQmseecDTPztiEc0C43P6E4RiKKjUJE=','c)RAv:5T/KgypDGy6.}\\3Y@b2:6En6'),(51,'Marcus Shannon','marcus_shannon','7S+LsWqMAMzrBU8kF9NvnhV/5GitX2RdIuhB5F1rDCU=','L\\2W%nCVHA$/i6OECmuODe5mx8G|DE'),(61,'Neva Kidd','neva_kidd','QvXIgmfkVXbf6NOGVE9BLhpWIpP+vye1jpDmZvGwtVM=','cY4R<94Sp(junI&B4X.%;(m\\0?8Jy.'),(71,'Weston Rasmussen','weston_rasmussen','i7RM92Cbg7TK3bxPl0jkFyRtMT6zcILHZ7/NDB0xUEE=','dUhB-L>Q~<7aM/[@rD/*9SCCze>*_B');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer`
--

DROP TABLE IF EXISTS `transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transfer` (
  `transfer_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `source_account_id` int(10) unsigned DEFAULT NULL,
  `destination_account_id` int(10) unsigned DEFAULT NULL,
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` bigint(20) unsigned NOT NULL,
  `cause` tinytext NOT NULL,
  PRIMARY KEY (`transfer_id`),
  KEY `source_account_id` (`source_account_id`),
  KEY `destination_account_id` (`destination_account_id`),
  CONSTRAINT `transfer_ibfk_1` FOREIGN KEY (`source_account_id`) REFERENCES `account` (`account_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `transfer_ibfk_2` FOREIGN KEY (`destination_account_id`) REFERENCES `account` (`account_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer`
--

LOCK TABLES `transfer` WRITE;
/*!40000 ALTER TABLE `transfer` DISABLE KEYS */;
INSERT INTO `transfer` VALUES (1,111,131,'2020-01-11 00:00:00',50000,'restituzione del prestito di natura infruttifera'),(11,151,121,'2020-02-11 00:00:00',1500000,'regalo per acquisto auto'),(21,111,151,'2020-03-11 00:00:00',150000,'giroconto'),(31,151,111,'2020-04-14 20:18:37',42069,'Epic shopping'),(41,151,141,'2020-01-11 00:00:00',500011,'prestito (grazie)'),(51,121,151,'2020-02-11 00:00:00',50000,'Rimborso spesa viaggio \"d\'affari\" ;)'),(61,141,131,'2020-03-11 00:00:00',15000,'prestito'),(71,121,131,'2020-04-14 20:21:30',30000,'Spesa gita duemilamai'),(81,161,111,'2020-01-11 00:00:00',50223,'computer'),(91,141,151,'2020-02-11 00:00:00',200000,'rimborso spese'),(101,161,131,'2020-03-11 00:00:00',100000,'buon amici'),(111,161,121,'2020-04-14 20:24:02',144800,'party in las vegas'),(121,121,141,'2020-01-11 00:00:00',70000,'Spesa online'),(131,141,161,'2020-02-11 00:00:00',51477,'calcolatrice'),(141,131,151,'2020-03-11 00:00:00',250000,'rimborso spese'),(151,131,151,'2020-04-14 20:26:32',91311,'bottiglietta di plastica');
/*!40000 ALTER TABLE `transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `account_total_amount`
--

/*!50001 DROP TABLE IF EXISTS `account_total_amount`*/;
/*!50001 DROP VIEW IF EXISTS `account_total_amount`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`b702d301000bee`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `account_total_amount` AS select `account`.`account_id` AS `account_id`,((`account`.`deposited_amount` + (select coalesce(sum(`transfer`.`amount`),0) from `transfer` where (`transfer`.`destination_account_id` = `account`.`account_id`))) - (select coalesce(sum(`transfer`.`amount`),0) from `transfer` where (`transfer`.`source_account_id` = `account`.`account_id`))) AS `total_amount` from `account` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-04-14 22:41:57
