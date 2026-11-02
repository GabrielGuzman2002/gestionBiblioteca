/*
SQLyog Community v13.2.1 (64 bit)
MySQL - 26.10.0 : Database - gestionbiblioteca
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`gestionbiblioteca` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gestionbiblioteca`;

/*Table structure for table `cuentas` */

DROP TABLE IF EXISTS `cuentas`;

CREATE TABLE `cuentas` (
  `id_cuenta` int NOT NULL AUTO_INCREMENT,
  `nombre_usuario` varchar(50) NOT NULL,
  `contraseña` varchar(255) NOT NULL,
  `cargo` varchar(20) NOT NULL DEFAULT 'BIBLIOTECARIO',
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` date NOT NULL,
  PRIMARY KEY (`id_cuenta`),
  UNIQUE KEY `nombre_usuario` (`nombre_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `cuentas` */

LOCK TABLES `cuentas` WRITE;

UNLOCK TABLES;

/*Table structure for table `libro` */

DROP TABLE IF EXISTS `libro`;

CREATE TABLE `libro` (
  `id_codigo` varchar(20) NOT NULL,
  `isbn` varchar(20) NOT NULL,
  `paginas` int NOT NULL,
  PRIMARY KEY (`id_codigo`),
  CONSTRAINT `libro_ibfk_1` FOREIGN KEY (`id_codigo`) REFERENCES `material_bibliografico` (`id_codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `libro` */

LOCK TABLES `libro` WRITE;

UNLOCK TABLES;

/*Table structure for table `material_bibliografico` */

DROP TABLE IF EXISTS `material_bibliografico`;

CREATE TABLE `material_bibliografico` (
  `id_codigo` varchar(20) NOT NULL,
  `titulov` varchar(150) NOT NULL,
  `autor` varchar(100) NOT NULL,
  `categoria` varchar(25) DEFAULT NULL,
  `fecha_registro` date DEFAULT NULL,
  `disponible` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id_codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `material_bibliografico` */

LOCK TABLES `material_bibliografico` WRITE;

UNLOCK TABLES;

/*Table structure for table `prestamo` */

DROP TABLE IF EXISTS `prestamo`;

CREATE TABLE `prestamo` (
  `id_prestamo` varchar(20) NOT NULL,
  `fecha_prestamo` date DEFAULT NULL,
  `fecha_devolucion` date DEFAULT NULL,
  `fecha_devolucion_prevista` date DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `id_usuario` varchar(20) NOT NULL,
  `id_codigo` varchar(20) NOT NULL,
  PRIMARY KEY (`id_prestamo`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_codigo` (`id_codigo`),
  CONSTRAINT `prestamo_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `prestamo_ibfk_2` FOREIGN KEY (`id_codigo`) REFERENCES `material_bibliografico` (`id_codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `prestamo` */

LOCK TABLES `prestamo` WRITE;

UNLOCK TABLES;

/*Table structure for table `revista` */

DROP TABLE IF EXISTS `revista`;

CREATE TABLE `revista` (
  `id_codigo` varchar(20) NOT NULL,
  `numero_edicion` int NOT NULL,
  `periodicidad` varchar(20) NOT NULL,
  `volumen` int DEFAULT NULL,
  PRIMARY KEY (`id_codigo`),
  CONSTRAINT `revista_ibfk_1` FOREIGN KEY (`id_codigo`) REFERENCES `material_bibliografico` (`id_codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `revista` */

LOCK TABLES `revista` WRITE;

UNLOCK TABLES;

/*Table structure for table `usuario` */

DROP TABLE IF EXISTS `usuario`;

CREATE TABLE `usuario` (
  `id_usuario` varchar(20) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `telefono` varchar(12) NOT NULL,
  `correo_electronico` varchar(150) DEFAULT NULL,
  `fecha_registro` date DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `correo_electronico` (`correo_electronico`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `usuario` */

LOCK TABLES `usuario` WRITE;

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
