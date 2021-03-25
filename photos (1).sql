-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Mar 25, 2021 at 05:11 PM
-- Server version: 5.7.31
-- PHP Version: 7.3.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rdv`
--

-- --------------------------------------------------------

--
-- Table structure for table `photos`
--

DROP TABLE IF EXISTS `photos`;
CREATE TABLE IF NOT EXISTS `photos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_path` varchar(100) DEFAULT NULL,
  `image_name` varchar(100) NOT NULL,
  `cd_user` varchar(10) NOT NULL,
  `direcao` varchar(30) NOT NULL,
  `gerencia` varchar(30) NOT NULL,
  `equipe` varchar(30) NOT NULL,
  `id_desp` int(11) NOT NULL,
  `valor_desp` decimal(10,2) NOT NULL,
  `km` int(11) NOT NULL,
  `obs` varchar(100) NOT NULL,
  `data` date DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `obsRep` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `photos`
--

INSERT INTO `photos` (`id`, `image_path`, `image_name`, `cd_user`, `direcao`, `gerencia`, `equipe`, `id_desp`, `valor_desp`, `km`, `obs`, `data`, `status`, `obsRep`) VALUES
(36, 'uploadExample/uploads/a.jpg', 'a', '', '', '', '', 0, '0.00', 0, '', NULL, '', NULL),
(35, 'uploadExample/uploads/q.jpg', 'q', '', '', '', '', 0, '0.00', 0, '', NULL, '', NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
