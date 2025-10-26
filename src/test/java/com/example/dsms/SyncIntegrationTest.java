package com.example.dsms;

import com.example.dsms.model.Vente;
import com.example.dsms.repository.dakar.VenteRepositoryDakar;
import com.example.dsms.repository.stl.VenteRepositoryStl;
import com.example.dsms.repository.thies.VenteRepositoryThies;
import com.example.dsms.service.SyncService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SyncIntegrationTest {

    @Autowired
    private VenteRepositoryDakar dakarRepo;
    @Autowired
    private VenteRepositoryThies thiesRepo;
    @Autowired
    private VenteRepositoryStl stlRepo;
    @Autowired
    private SyncService syncService;

    private static UUID testId;

    @Test
    @Order(1)
    public void createInDakar() {
        Vente v = new Vente();
        testId = UUID.randomUUID();
        v.setId(testId);
        v.setProduit("TestProduit");
        v.setMontant(100.0);
        v.setDateVente(LocalDate.now());
        v.setRegion("Dakar");

        dakarRepo.save(v);
        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente non créée dans Dakar");
    }

    @Test
    @Order(2)
    public void syncAndCheckReplication() throws InterruptedException {
        syncService.syncAll();
        Thread.sleep(1000);

        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente non répliquée à Thiès");
        assertTrue(stlRepo.findById(testId).isPresent(), "Vente non répliquée à Saint-Louis");
    }



}

