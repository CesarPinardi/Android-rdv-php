-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Tempo de geração: 25-Mar-2021 às 20:09
-- Versão do servidor: 10.4.14-MariaDB
-- versão do PHP: 7.4.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `rdv`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `users`
--

CREATE TABLE `users` (
  `id` int(20) NOT NULL,
  `username` varchar(100) CHARACTER SET latin1 NOT NULL,
  `password` varchar(32) CHARACTER SET latin1 NOT NULL,
  `gerencia` varchar(30) CHARACTER SET latin1 DEFAULT NULL,
  `equipe` varchar(30) CHARACTER SET latin1 DEFAULT NULL,
  `direcao` varchar(30) CHARACTER SET latin1 DEFAULT NULL,
  `cd_user` varchar(10) NOT NULL,
  `ativo` char(1) NOT NULL DEFAULT 'A'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `gerencia`, `equipe`, `direcao`, `cd_user`, `ativo`) VALUES
(10, 'ADM', 'adm', 'DIRECAO', 'DIRECAO', 'DIRECAO', 'ADM', 'A'),
(7, 'LUIS PEDROSA', 'VET@1234', 'DIRECAO', 'DIRECAO', 'DIRECAO', 'LUIS', 'A'),
(9, 'DELEUSE EVELISE GARDIANO', 'Vet@123', 'GERENCIA VSA', 'S&A AMARELA', 'DIRECAO', 'DELEUSEL', 'A');

--
-- Índices para tabelas despejadas
--

--
-- Índices para tabela `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`username`);

--
-- AUTO_INCREMENT de tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `users`
--
ALTER TABLE `users`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
