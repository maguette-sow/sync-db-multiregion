-- SQL script to create table in each DB (run on each database)
CREATE TABLE IF NOT EXISTS vente (
  id UUID PRIMARY KEY,
  date_vente DATE,
  montant DECIMAL(10,2),
  produit VARCHAR(255),
  region VARCHAR(100)
);
