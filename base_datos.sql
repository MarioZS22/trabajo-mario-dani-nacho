-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.32-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para trabajo
CREATE DATABASE IF NOT EXISTS `trabajo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `trabajo`;

-- Volcando estructura para tabla trabajo.detallespartida
CREATE TABLE IF NOT EXISTS `detallespartida` (
  `IdPartida` int(11) NOT NULL,
  `IdJugador` int(11) NOT NULL,
  `Casilla` int(11) DEFAULT NULL,
  `Turno` int(11) DEFAULT NULL,
  `Bloqueos` bit(1) DEFAULT NULL,
  `Orden` int(11) DEFAULT NULL,
  PRIMARY KEY (`IdPartida`,`IdJugador`) USING BTREE,
  KEY `FK_detallespartida_jugadores` (`IdJugador`) USING BTREE,
  KEY `FK_detallespartida_tablero` (`Casilla`) USING BTREE,
  CONSTRAINT `FK_detallespartida_jugadores` FOREIGN KEY (`IdJugador`) REFERENCES `jugadores` (`IdJugador`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_detallespartida_partidas` FOREIGN KEY (`IdPartida`) REFERENCES `partidas` (`IdPartida`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_detallespartida_tablero` FOREIGN KEY (`Casilla`) REFERENCES `tablero` (`Casilla`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla trabajo.detallespartida: ~1 rows (aproximadamente)
INSERT INTO `detallespartida` (`IdPartida`, `IdJugador`, `Casilla`, `Turno`, `Bloqueos`, `Orden`) VALUES
	(3, 5, 1, NULL, NULL, NULL);

-- Volcando estructura para tabla trabajo.jugadores
CREATE TABLE IF NOT EXISTS `jugadores` (
  `IdJugador` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(50) NOT NULL,
  `Password` varchar(50) NOT NULL,
  PRIMARY KEY (`IdJugador`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla trabajo.jugadores: ~6 rows (aproximadamente)
INSERT INTO `jugadores` (`IdJugador`, `Nombre`, `Password`) VALUES
	(1, 'Pepe', 'a'),
	(2, 'luis', 'b'),
	(3, 'pepe', '1234'),
	(4, 'jesulin', '123'),
	(5, 'plopmar1', '1'),
	(6, 'danisilva', 'leire');

-- Volcando estructura para tabla trabajo.partidas
CREATE TABLE IF NOT EXISTS `partidas` (
  `IdPartida` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(50) DEFAULT NULL,
  `IdEstado` int(11) DEFAULT NULL,
  `TurnoDe` int(11) DEFAULT 1,
  `IdGanador` int(11) DEFAULT NULL,
  `Estado` int(11) DEFAULT 0,
  PRIMARY KEY (`IdPartida`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla trabajo.partidas: ~1 rows (aproximadamente)
INSERT INTO `partidas` (`IdPartida`, `Nombre`, `IdEstado`, `TurnoDe`, `IdGanador`, `Estado`) VALUES
	(3, 'Partida de plopmar1', NULL, 5, NULL, 0);

-- Volcando estructura para tabla trabajo.tablero
CREATE TABLE IF NOT EXISTS `tablero` (
  `Casilla` int(11) NOT NULL,
  `IdTipo` int(11) DEFAULT NULL,
  PRIMARY KEY (`Casilla`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla trabajo.tablero: ~63 rows (aproximadamente)
INSERT INTO `tablero` (`Casilla`, `IdTipo`) VALUES
	(1, 2),
	(2, 1),
	(3, 1),
	(4, 1),
	(5, 2),
	(6, 3),
	(7, 1),
	(8, 1),
	(9, 2),
	(10, 1),
	(11, 1),
	(12, 3),
	(13, 1),
	(14, 2),
	(15, 1),
	(16, 1),
	(17, 1),
	(18, 2),
	(19, 4),
	(20, 1),
	(21, 1),
	(22, 1),
	(23, 2),
	(24, 1),
	(25, 1),
	(26, 10),
	(27, 2),
	(28, 1),
	(29, 1),
	(30, 1),
	(31, 5),
	(32, 1),
	(33, 1),
	(34, 1),
	(35, 1),
	(36, 2),
	(37, 1),
	(38, 1),
	(39, 1),
	(40, 1),
	(41, 2),
	(42, 6),
	(43, 1),
	(44, 1),
	(45, 2),
	(46, 1),
	(47, 1),
	(48, 1),
	(49, 1),
	(50, 2),
	(51, 1),
	(52, 7),
	(53, 10),
	(54, 2),
	(55, 1),
	(56, 1),
	(57, 1),
	(58, 8),
	(59, 2),
	(60, 1),
	(61, 1),
	(62, 1),
	(63, 9);

-- Volcando estructura para tabla trabajo.tipocasilla
CREATE TABLE IF NOT EXISTS `tipocasilla` (
  `IdTipo` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`IdTipo`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla trabajo.tipocasilla: ~10 rows (aproximadamente)
INSERT INTO `tipocasilla` (`IdTipo`, `Nombre`) VALUES
	(1, 'Normal'),
	(2, 'Oca'),
	(3, 'Puente'),
	(4, 'Posada'),
	(5, 'Pozo'),
	(6, 'Laberinto'),
	(7, 'Carcel'),
	(8, 'Calavera'),
	(9, 'Meta'),
	(10, 'Dado');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
