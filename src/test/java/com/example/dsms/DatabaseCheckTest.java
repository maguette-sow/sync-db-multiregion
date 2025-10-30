package com.example.dsms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DatabaseCheckTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testVenteTableExists() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            // Vérifie la présence de la table 'vente' dans la base de test
            ResultSet rs = stmt.executeQuery(
                    "SHOW TABLES LIKE 'vente';"
            );

            boolean tableExists = rs.next();
            System.out.println("✅ Tables existantes dans la base de test : ");
            ResultSet allTables = stmt.executeQuery("SHOW TABLES;");
            while (allTables.next()) {
                System.out.println(" - " + allTables.getString(1));
            }

            assertTrue(tableExists, "❌ La table 'vente' n'existe pas dans la base de test !");
            System.out.println("✅ La table 'vente' existe bien dans la base Testcontainers.");
        }
    }
}
