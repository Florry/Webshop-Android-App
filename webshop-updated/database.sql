CREATE DATABASE  IF NOT EXISTS `web_interface` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `web_interface`;
-- MySQL dump 10.13  Distrib 5.6.13, for osx10.6 (i386)
--
-- Host: 127.0.0.1    Database: web_interface
-- ------------------------------------------------------
-- Server version	5.6.15

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
-- Table structure for table `CategoryModel`
--

DROP TABLE IF EXISTS `CategoryModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CategoryModel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `staff_responsible` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CategoryModel`
--

LOCK TABLES `CategoryModel` WRITE;
/*!40000 ALTER TABLE `CategoryModel` DISABLE KEYS */;
INSERT INTO `CategoryModel` VALUES (1,'Fruit',2),(2,'Books',2),(3,'Films',2),(4,'CD-ROMs',2),(5,'Headsets',2),(6,'Tools',2),(7,'Locomotives',2);
/*!40000 ALTER TABLE `CategoryModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `OrderModel`
--

DROP TABLE IF EXISTS `OrderModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OrderModel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` varchar(255) DEFAULT NULL,
  `totalPrice` int(11) NOT NULL,
  `user` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_i3omeyn8y775egskodfml58fn` (`user`),
  CONSTRAINT `FK_i3omeyn8y775egskodfml58fn` FOREIGN KEY (`user`) REFERENCES `UserModel` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `OrderModel`
--

LOCK TABLES `OrderModel` WRITE;
/*!40000 ALTER TABLE `OrderModel` DISABLE KEYS */;
INSERT INTO `OrderModel` VALUES (1,'2014/04/09 09:34:25',155,'admin');
/*!40000 ALTER TABLE `OrderModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `OrderModel_ProductInCartModel`
--

DROP TABLE IF EXISTS `OrderModel_ProductInCartModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OrderModel_ProductInCartModel` (
  `OrderModel_id` int(11) NOT NULL,
  `products_id` int(11) NOT NULL,
  UNIQUE KEY `UK_4543m86endbx7letiav9i999p` (`products_id`),
  KEY `FK_4543m86endbx7letiav9i999p` (`products_id`),
  KEY `FK_hryisrua3x16inr10ygirvyj8` (`OrderModel_id`),
  CONSTRAINT `FK_hryisrua3x16inr10ygirvyj8` FOREIGN KEY (`OrderModel_id`) REFERENCES `OrderModel` (`id`),
  CONSTRAINT `FK_4543m86endbx7letiav9i999p` FOREIGN KEY (`products_id`) REFERENCES `ProductInCartModel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `OrderModel_ProductInCartModel`
--

LOCK TABLES `OrderModel_ProductInCartModel` WRITE;
/*!40000 ALTER TABLE `OrderModel_ProductInCartModel` DISABLE KEYS */;
INSERT INTO `OrderModel_ProductInCartModel` VALUES (1,1),(1,2);
/*!40000 ALTER TABLE `OrderModel_ProductInCartModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ProductInCartModel`
--

DROP TABLE IF EXISTS `ProductInCartModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProductInCartModel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL,
  `quantity_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_4eeghscw9uy9m79wm97veopsp` (`product_id`),
  KEY `FK_emn3knbgn27yrbpdmlhgncswj` (`quantity_id`),
  CONSTRAINT `FK_emn3knbgn27yrbpdmlhgncswj` FOREIGN KEY (`quantity_id`) REFERENCES `QuantityModel` (`id`),
  CONSTRAINT `FK_4eeghscw9uy9m79wm97veopsp` FOREIGN KEY (`product_id`) REFERENCES `ProductModel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ProductInCartModel`
--

LOCK TABLES `ProductInCartModel` WRITE;
/*!40000 ALTER TABLE `ProductInCartModel` DISABLE KEYS */;
INSERT INTO `ProductInCartModel` VALUES (1,1,4),(2,4,3);
/*!40000 ALTER TABLE `ProductInCartModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ProductModel`
--

DROP TABLE IF EXISTS `ProductModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProductModel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cost` double NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `imgUrl` varchar(255) DEFAULT NULL,
  `inStore` tinyint(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `productType` int(11) NOT NULL,
  `rrp` double NOT NULL,
  `stock` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ProductModel`
--

LOCK TABLES `ProductModel` WRITE;
/*!40000 ALTER TABLE `ProductModel` DISABLE KEYS */;
INSERT INTO `ProductModel` VALUES (1,40,'Small round fruit, tasty. Both green and red in color.','http://www.sprayitaway.com/wp-content/uploads/2013/08/apple_by_grv422-d5554a4.jpg',0,'Apple',2,20,0),(2,22,'Small round fruit, tasty. Only comes in orange, as the name suggests','http://www.plitka.eu/published/publicdata/PLITKAT/attachments/SC/products_pictures/plitka-tile-EU-SP2-Navarti-Ceramica-Golf-Orange-450-450-434-425-.jpg',1,'Orange',2,20,0),(3,10,'Small pear-shaped fruit, tasty. Comes in many colors including red, black and yellow.','http://www.visitrockypoint.com/wp-content/uploads/2014/01/pear_1.jpg',1,'Pear',2,20,0),(4,35,'A hammer is used to hammer stuff.','http://www.pachd.com/free-images/household-images/hammer-01.jpg',1,'Hammer',2,20,0),(5,15,'Not the drink.','http://3.bp.blogspot.com/-YJ5K4CJRCK0/UaNezxzHodI/AAAAAAAAIzg/XKQdQkJQGa4/s1600/Harvey+wallbanger.jpg',1,'Screwdriver',2,20,0),(6,1000,'In other words, a train','http://upload.wikimedia.org/wikipedia/commons/9/95/M62_diesel_locomotive_from_Luninets_depot.jpg',1,'Locomotive',2,20,0);
/*!40000 ALTER TABLE `ProductModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ProductModel_CategoryModel`
--

DROP TABLE IF EXISTS `ProductModel_CategoryModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProductModel_CategoryModel` (
  `products_id` int(11) NOT NULL,
  `categories_id` int(11) NOT NULL,
  KEY `FK_gcc87b373r6a6wa56r6a2wxpu` (`categories_id`),
  KEY `FK_m6vqs37ovstadw79ctq3tjb6` (`products_id`),
  CONSTRAINT `FK_m6vqs37ovstadw79ctq3tjb6` FOREIGN KEY (`products_id`) REFERENCES `ProductModel` (`id`),
  CONSTRAINT `FK_gcc87b373r6a6wa56r6a2wxpu` FOREIGN KEY (`categories_id`) REFERENCES `CategoryModel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ProductModel_CategoryModel`
--

LOCK TABLES `ProductModel_CategoryModel` WRITE;
/*!40000 ALTER TABLE `ProductModel_CategoryModel` DISABLE KEYS */;
INSERT INTO `ProductModel_CategoryModel` VALUES (1,1),(2,1),(3,1),(4,6),(5,6),(6,7);
/*!40000 ALTER TABLE `ProductModel_CategoryModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QuantityModel`
--

DROP TABLE IF EXISTS `QuantityModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QuantityModel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QuantityModel`
--

LOCK TABLES `QuantityModel` WRITE;
/*!40000 ALTER TABLE `QuantityModel` DISABLE KEYS */;
INSERT INTO `QuantityModel` VALUES (1,1),(2,2),(3,1),(4,3);
/*!40000 ALTER TABLE `QuantityModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserModel`
--

DROP TABLE IF EXISTS `UserModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserModel` (
  `email` varchar(255) NOT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `postcode` varchar(255) DEFAULT NULL,
  `rights` int(11) NOT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `town` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserModel`
--

LOCK TABLES `UserModel` WRITE;
/*!40000 ALTER TABLE `UserModel` DISABLE KEYS */;
INSERT INTO `UserModel` VALUES ('admin','n/a','n/a','n/a','n/a','n/a','admin','n/a',1,'n/a','n/a'),('cmon@letsgo.com','TORSGATAN 44','C/O Nonsense','2012-02-01','Tom','Whitemore','password','14462',0,'0704596049','Stockholm'),('lefi@1337.se','Bergvägen 4','C/O ','Bergvägen 4','Sam','Bobson','Test','14462',0,'0704596049','Stockholm'),('Persson@persson.se','Strandvägen 2','','1943-08-12','Olle','Persson','ollerockz','14462',0,'0703487564','Stockholm'),('viktor.soderstrom@live.se','Säterstigen 13','','1942-02-01','Viktor','Söderström','okok007','14462',0,'0703209874','Rönninge');
/*!40000 ALTER TABLE `UserModel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserModel_ProductInCartModel`
--

DROP TABLE IF EXISTS `UserModel_ProductInCartModel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserModel_ProductInCartModel` (
  `UserModel_email` varchar(255) NOT NULL,
  `products_id` int(11) NOT NULL,
  UNIQUE KEY `UK_gl6somu9f79t41wyie302859w` (`products_id`),
  KEY `FK_gl6somu9f79t41wyie302859w` (`products_id`),
  KEY `FK_bycs54wrel80huiblg6m6nmb9` (`UserModel_email`),
  CONSTRAINT `FK_bycs54wrel80huiblg6m6nmb9` FOREIGN KEY (`UserModel_email`) REFERENCES `UserModel` (`email`),
  CONSTRAINT `FK_gl6somu9f79t41wyie302859w` FOREIGN KEY (`products_id`) REFERENCES `ProductInCartModel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserModel_ProductInCartModel`
--

LOCK TABLES `UserModel_ProductInCartModel` WRITE;
/*!40000 ALTER TABLE `UserModel_ProductInCartModel` DISABLE KEYS */;
/*!40000 ALTER TABLE `UserModel_ProductInCartModel` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-04-09  9:49:26
