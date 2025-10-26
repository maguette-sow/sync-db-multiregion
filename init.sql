-- Création de l'utilisateur global
CREATE USER IF NOT EXISTS 'nogaye'@'%' IDENTIFIED BY 'sow2004';

-- Création des 3 bases régionales
CREATE DATABASE IF NOT EXISTS ventes_dakar CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ventes_thies CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ventes_stlouis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Attribution des privilèges à l'utilisateur
GRANT ALL PRIVILEGES ON ventes_dakar.* TO 'nogaye'@'%';
GRANT ALL PRIVILEGES ON ventes_thies.* TO 'nogaye'@'%';
GRANT ALL PRIVILEGES ON ventes_stlouis.* TO 'nogaye'@'%';

-- Rafraîchissement des privilèges
FLUSH PRIVILEGES;
