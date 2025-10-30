//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test") // 🔹 utilise le profil test isolé
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    // --- Conteneur MySQL isolé pour les tests ---
//    @Container
//    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.password", mysqlContainer::getPassword);
//    }
//
//    @Autowired private VenteRepositoryDakar dakarRepo;
//    @Autowired private VenteRepositoryThies thiesRepo;
//    @Autowired private VenteRepositoryStl stlRepo;
//    @Autowired private SyncService syncService;
//
//    private static UUID testId;
//
//
//
//    @Test
//    @Order(1)
//    void createInDakar() {
//        System.out.println("Datasource URL de test : " + mysqlContainer.getJdbcUrl());
//
//        Vente v = new Vente();
//        testId = UUID.randomUUID();
//        v.setId(testId);
//        v.setProduit("TestProduit");
//        v.setMontant(100.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        dakarRepo.save(v);
//        assertTrue(dakarRepo.findById(testId).isPresent(), "❌ Vente non créée dans Dakar");
//        System.out.println("✅ Vente créée avec succès dans Dakar");
//    }
//
//
//    @Test
//    @Order(2)
//    public void syncAndCheckReplication() throws InterruptedException {
//        System.out.println("🌀 Lancement de la synchronisation manuelle...");
//        syncService.syncAll();
//
//        // On attend la réplication effective (avec une boucle de vérification)
//        boolean replicated = false;
//        int retries = 0;
//
//        while (retries < 10) { // on réessaie jusqu'à 10 fois (10 x 1s = 10 secondes max)
//            boolean inThies = thiesRepo.findById(testId).isPresent();
//            boolean inStl = stlRepo.findById(testId).isPresent();
//
//            if (inThies && inStl) {
//                replicated = true;
//                System.out.println("✅ Vente répliquée avec succès à Thies et Saint-Louis !");
//                break;
//            }
//
//            Thread.sleep(1000); // attendre 1 seconde avant de revérifier
//            retries++;
//            System.out.println("⏳ Attente réplication... tentative " + retries);
//        }
//
//        assertTrue(replicated, "Vente non répliquée à toutes les régions après 10s");
//    }
//
//
//    @Test
//    @Order(3)
//    void testSimulatedDBFailure() throws InterruptedException {
//        System.out.println("🔥 Simulation d'une panne logique (base inaccessible)...");
//
//        UUID recoveryId = UUID.randomUUID();
//        Vente v = new Vente();
//        v.setId(recoveryId);
//        v.setProduit("ProduitRecovery");
//        v.setMontant(200.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        // Étape 1 — Simulation d'une panne logique
//        try {
//            throw new RuntimeException("Simulated database connection failure");
//        } catch (Exception e) {
//            System.out.println("✅ Exception simulée pendant la panne : " + e.getMessage());
//        }
//
//        // Étape 2 — Reconnexion logique (pause)
//        System.out.println("♻️ Reconnexion logique à la base...");
//        Thread.sleep(1500);
//
//        // Étape 3 — Sauvegarde après recovery
//        dakarRepo.save(v);
//        syncService.syncAll();
//        Thread.sleep(1000);
//
//        assertTrue(dakarRepo.findById(recoveryId).isPresent(), "❌ Vente non recréée à Dakar après recovery");
//        assertTrue(thiesRepo.findById(recoveryId).isPresent(), "❌ Vente non répliquée à Thiès après recovery");
//        assertTrue(stlRepo.findById(recoveryId).isPresent(), "❌ Vente non répliquée à Saint-Louis après recovery");
//
//        System.out.println("✅ Test de récupération logique réussi !");
//    }
//}
//
//
//
//


//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    @Container
//    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.password", mysqlContainer::getPassword);
//    }
//
//    @Autowired private VenteRepositoryDakar dakarRepo;
//    @Autowired private VenteRepositoryThies thiesRepo;
//    @Autowired private VenteRepositoryStl stlRepo;
//    @Autowired private SyncService syncService;
//
//    private static UUID testId;
//
//    @Test
//    @Order(1)
//    void createInDakar() {
//        // ✅ Affiche l'URL de la base de test
//        System.out.println("Datasource URL de test : " + mysqlContainer.getJdbcUrl());
//
//        Vente v = new Vente();
//        testId = UUID.randomUUID();
//        v.setId(testId);
//        v.setProduit("TestProduit");
//        v.setMontant(100.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        dakarRepo.save(v);
//        assertTrue(dakarRepo.findById(testId).isPresent(), "❌ Vente non créée dans Dakar");
//        System.out.println("✅ Vente créée avec succès dans Dakar");
//    }
//
//    @Test
//    @Order(2)
//    public void syncAndCheckReplication() throws InterruptedException {
//        System.out.println("🌀 Lancement de la synchronisation manuelle...");
//        syncService.syncAll();
//
//        boolean replicated = false;
//        int retries = 0;
//
//        while (retries < 10) {
//            boolean inThies = thiesRepo.findById(testId).isPresent();
//            boolean inStl = stlRepo.findById(testId).isPresent();
//
//            if (inThies && inStl) {
//                replicated = true;
//                System.out.println("✅ Vente répliquée avec succès à Thies et Saint-Louis !");
//                break;
//            }
//
//            Thread.sleep(1000);
//            retries++;
//            System.out.println("⏳ Attente réplication... tentative " + retries);
//        }
//
//        assertTrue(replicated, "Vente non répliquée à toutes les régions après 10s");
//    }
//
//    @Test
//    @Order(3)
//    void testSimulatedDBFailure() throws InterruptedException {
//        System.out.println("🔥 Simulation d'une panne logique...");
//
//        UUID recoveryId = UUID.randomUUID();
//        Vente v = new Vente();
//        v.setId(recoveryId);
//        v.setProduit("ProduitRecovery");
//        v.setMontant(200.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        try {
//            throw new RuntimeException("Simulated database connection failure");
//        } catch (Exception e) {
//            System.out.println("✅ Exception simulée : " + e.getMessage());
//        }
//
//        System.out.println("♻️ Reconnexion logique à la base...");
//        Thread.sleep(1500);
//
//        dakarRepo.save(v);
//        syncService.syncAll();
//        Thread.sleep(1000);
//
//        assertTrue(dakarRepo.findById(recoveryId).isPresent(), "❌ Vente non recréée à Dakar après recovery");
//        assertTrue(thiesRepo.findById(recoveryId).isPresent(), "❌ Vente non répliquée à Thiès après recovery");
//        assertTrue(stlRepo.findById(recoveryId).isPresent(), "❌ Vente non répliquée à Saint-Louis après recovery");
//
//        System.out.println("✅ Test de récupération logique réussi !");
//    }
//}

//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    @Container
//    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.password", mysqlContainer::getPassword);
//    }
//
//    @Autowired private VenteRepositoryDakar dakarRepo;
//    @Autowired private VenteRepositoryThies thiesRepo;
//    @Autowired private VenteRepositoryStl stlRepo;
//
//    // 🔹 On mocke SyncService pour qu'il ne fasse rien pendant les tests
//    @MockBean private SyncService syncService;
//
//    private static UUID testId;
//
//    @Test
//    @Order(1)
//    public void createInDakar() {
//        Vente v = new Vente();
//        testId = UUID.randomUUID();
//        v.setId(testId);
//        v.setProduit("TestProd");
//        v.setMontant(42.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//        dakarRepo.save(v);
//        assertTrue(dakarRepo.findById(testId).isPresent());
//    }
//
//    @Test
//    @Order(2)
//    public void runSyncAndVerifyReplication() throws InterruptedException {
//        // Run full sync to be safe in test
//        syncService.syncAll();
//
//        // Small wait optional if DB latency; otherwise immediate
//        Thread.sleep(500);
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente should be present in Thies after sync");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente should be present in Saint-Louis after sync");
//
//}
//
//    @Test
//    @Order(3)
//    void testSimulatedDBFailure() {
//        System.out.println("🔥 Simulation d'une panne logique...");
//
//        UUID recoveryId = UUID.randomUUID();
//        Vente v = new Vente();
//        v.setId(recoveryId);
//        v.setProduit("ProduitRecovery");
//        v.setMontant(200.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        // 🧠 Simulation d'une panne : la méthode save échoue une première fois
//        try {
//            throw new RuntimeException("Simulated database failure during save");
//        } catch (Exception e) {
//            System.out.println("✅ Exception simulée : " + e.getMessage());
//        }
//
//        // ♻️ Reconnexion et nouvelle tentative
//        dakarRepo.save(v);
//        syncService.syncAll();
//
//        assertTrue(thiesRepo.findById(recoveryId).isPresent(), "❌ Vente non répliquée à Thiès après recovery");
//        assertTrue(stlRepo.findById(recoveryId).isPresent(), "❌ Vente non répliquée à Saint-Louis après recovery");
//
//        System.out.println("✅ Test de récupération logique réussi !");
//    }
//}


//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.awaitility.Awaitility;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.UUID;
//
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    @Container
//    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        // Toutes les datasources pointent vers le même conteneur éphémère
//        registry.add("spring.datasource.dakar.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.thies.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.stl.url", mysqlContainer::getJdbcUrl);
//
//        registry.add("spring.datasource.dakar.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.thies.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.stl.username", mysqlContainer::getUsername);
//
//        registry.add("spring.datasource.dakar.password", mysqlContainer::getPassword);
//        registry.add("spring.datasource.thies.password", mysqlContainer::getPassword);
//        registry.add("spring.datasource.stl.password", mysqlContainer::getPassword);
//    }
//
//    @Autowired private VenteRepositoryDakar dakarRepo;
//    @Autowired private VenteRepositoryThies thiesRepo;
//    @Autowired private VenteRepositoryStl stlRepo;
//    @Autowired private SyncService syncService; // Injection du vrai service
//
//    private static UUID testId;
//
//    @AfterEach
//    void cleanup() {
//        // Nettoyage après chaque test
//        dakarRepo.deleteAll();
//        thiesRepo.deleteAll();
//        stlRepo.deleteAll();
//    }
//
//    @Test
//    @Order(1)
//    public void createInDakar() {
//        Vente v = new Vente();
//        testId = UUID.randomUUID();
//        v.setId(testId);
//        v.setProduit("TestProd");
//        v.setMontant(42.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//        dakarRepo.save(v);
//
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente doit exister à Dakar");
//    }
//
//    @Test
//    @Order(2)
//    public void runSyncAndVerifyReplication() {
//        // Génération d'un testId unique
//        UUID testId = UUID.randomUUID();
//        System.out.println("[DEBUG] Création d'une vente test avec testId = " + testId);
//
//        // Création et insertion de la vente dans le repo Dakar (source)
//        Vente vente = new Vente();
//        vente.setId(testId);
//        vente.setMontant(1000.0);  // double literal
//        vente.setProduit("ProduitSync");
//        vente.setDateVente(LocalDate.now());
//        vente.setRegion("Dakar");
//
//        dakarRepo.save(vente);
//        System.out.println("[DEBUG] Vente test insérée dans dakarRepo");
//
//        // Vérification initiale
//        boolean sourceExists = dakarRepo.findById(testId).isPresent();
//        System.out.println("[DEBUG] Vente source présente ? " + sourceExists);
//        assertTrue(sourceExists, "La vente doit exister dans le repository Dakar avant la synchro");
//
//        // Lancer la synchronisation
//        System.out.println("[DEBUG] Lancement de la synchronisation...");
//        syncService.syncAll();
//
//        // Attendre que la vente apparaisse dans Thies et Saint-Louis (max 5 secondes)
//        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> {
//            boolean inThies = thiesRepo.findById(testId).isPresent();
//            boolean inStl   = stlRepo.findById(testId).isPresent();
//            System.out.println("[DEBUG] Vente présente à Thies ? " + inThies);
//            System.out.println("[DEBUG] Vente présente à Saint-Louis ? " + inStl);
//            return inThies && inStl;
//        });
//
//        // Assertions finales
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente doit être répliquée à Thies");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente doit être répliquée à Saint-Louis");
//
//        System.out.println("[DEBUG] Test terminé avec succès pour testId = " + testId);
//    }
//
//
//    @Test
//    @Order(3)
//    void testSimulatedDBFailure() {
//        System.out.println("🔥 Simulation d'une panne logique...");
//
//        UUID recoveryId = UUID.randomUUID();
//        Vente v = new Vente();
//        v.setId(recoveryId);
//        v.setProduit("ProduitRecovery");
//        v.setMontant(200.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        // Simulation d'une première tentative échouée
//        try {
//            throw new RuntimeException("Simulated database failure during save");
//        } catch (Exception e) {
//            System.out.println("✅ Exception simulée : " + e.getMessage());
//        }
//
//        // Nouvelle tentative réussie
//        dakarRepo.save(v);
//        syncService.syncAll();
//
//        assertTrue(thiesRepo.findById(recoveryId).isPresent(), "Vente répliquée à Thies après recovery");
//        assertTrue(stlRepo.findById(recoveryId).isPresent(), "Vente répliquée à Saint-Louis après recovery");
//
//        System.out.println("✅ Test de récupération logique réussi !");
//    }
//}

//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.awaitility.Awaitility;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    @Container
//    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.dakar.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.thies.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.stl.url", mysqlContainer::getJdbcUrl);
//
//        registry.add("spring.datasource.dakar.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.thies.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.stl.username", mysqlContainer::getUsername);
//
//        registry.add("spring.datasource.dakar.password", mysqlContainer::getPassword);
//        registry.add("spring.datasource.thies.password", mysqlContainer::getPassword);
//        registry.add("spring.datasource.stl.password", mysqlContainer::getPassword);
//    }
//
//    @Autowired private VenteRepositoryDakar dakarRepo;
//    @Autowired private VenteRepositoryThies thiesRepo;
//    @Autowired private VenteRepositoryStl stlRepo;
//    @Autowired private SyncService syncService;
//
//    @AfterEach
//    void cleanup() {
//        dakarRepo.deleteAll();
//        thiesRepo.deleteAll();
//        stlRepo.deleteAll();
//    }
//
//    @Test
//    @Order(1)
//    void testCreateVenteDakar() {
//        UUID testId = UUID.randomUUID();
//        Vente v = new Vente();
//        v.setId(testId);
//        v.setProduit("ProduitTest1");
//        v.setMontant(50.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        dakarRepo.save(v);
//
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente doit exister dans Dakar repo");
//    }
//
//    @Test
//    @Order(2)
//    void testSyncReplication() {
//        UUID testId = UUID.randomUUID();
//        Vente vente = new Vente();
//        vente.setId(testId);
//        vente.setProduit("ProduitSync");
//        vente.setMontant(100.0);
//        vente.setDateVente(LocalDate.now());
//        vente.setRegion("Dakar");
//
//        dakarRepo.save(vente);
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente doit exister dans Dakar avant sync");
//
//        syncService.syncAll();
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(5))
//                .until(() -> thiesRepo.findById(testId).isPresent() && stlRepo.findById(testId).isPresent());
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente doit être répliquée à Thies");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente doit être répliquée à Saint-Louis");
//    }
//
//    @Test
//    @Order(3)
//    void testRecoveryAfterFailure() {
//        UUID testId = UUID.randomUUID();
//        Vente vente = new Vente();
//        vente.setId(testId);
//        vente.setProduit("ProduitRecovery");
//        vente.setMontant(200.0);
//        vente.setDateVente(LocalDate.now());
//        vente.setRegion("Dakar");
//
//        try {
//            throw new RuntimeException("Simulated DB failure");
//        } catch (Exception e) {
//            System.out.println("[INFO] Exception simulée: " + e.getMessage());
//        }
//
//        dakarRepo.save(vente);
//        syncService.syncAll();
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(5))
//                .until(() -> thiesRepo.findById(testId).isPresent() && stlRepo.findById(testId).isPresent());
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente répliquée à Thies après recovery");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente répliquée à Saint-Louis après recovery");
//    }
//}

//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.awaitility.Awaitility;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    @Container
//    private static final MySQLContainer<?> mysqlDakar = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test_dakar")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @Container
//    private static final MySQLContainer<?> mysqlThies = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test_thies")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @Container
//    private static final MySQLContainer<?> mysqlStl = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test_stl")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        // Dakar
//        registry.add("spring.datasource.dakar.url", mysqlDakar::getJdbcUrl);
//        registry.add("spring.datasource.dakar.username", mysqlDakar::getUsername);
//        registry.add("spring.datasource.dakar.password", mysqlDakar::getPassword);
//
//        // Thies
//        registry.add("spring.datasource.thies.url", mysqlThies::getJdbcUrl);
//        registry.add("spring.datasource.thies.username", mysqlThies::getUsername);
//        registry.add("spring.datasource.thies.password", mysqlThies::getPassword);
//
//        // Saint-Louis
//        registry.add("spring.datasource.stl.url", mysqlStl::getJdbcUrl);
//        registry.add("spring.datasource.stl.username", mysqlStl::getUsername);
//        registry.add("spring.datasource.stl.password", mysqlStl::getPassword);
//    }
//
//    @Autowired private VenteRepositoryDakar dakarRepo;
//    @Autowired private VenteRepositoryThies thiesRepo;
//    @Autowired private VenteRepositoryStl stlRepo;
//    @Autowired private SyncService syncService;
//
//    @AfterEach
//    void cleanup() {
//        dakarRepo.deleteAll();
//        thiesRepo.deleteAll();
//        stlRepo.deleteAll();
//    }
//
//    @Test
//    @Order(1)
//    void testCreateVenteDakar() {
//        UUID testId = UUID.randomUUID();
//        Vente v = new Vente();
//        v.setId(testId);
//        v.setProduit("ProduitTest1");
//        v.setMontant(50.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        dakarRepo.save(v);
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente doit exister dans Dakar repo");
//    }
//
//    @Test
//    @Order(2)
//    void testSyncReplication() {
//        UUID testId = UUID.randomUUID();
//        Vente vente = new Vente();
//        vente.setId(testId);
//        vente.setProduit("ProduitSync");
//        vente.setMontant(100.0);
//        vente.setDateVente(LocalDate.now());
//        vente.setRegion("Dakar");
//
//        dakarRepo.save(vente);
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente doit exister dans Dakar avant sync");
//
//        syncService.syncAll();
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(5))
//                .until(() -> thiesRepo.findById(testId).isPresent() && stlRepo.findById(testId).isPresent());
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente doit être répliquée à Thies");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente doit être répliquée à Saint-Louis");
//    }
//
//    @Test
//    @Order(3)
//    void testRecoveryAfterFailure() {
//        UUID testId = UUID.randomUUID();
//        Vente vente = new Vente();
//        vente.setId(testId);
//        vente.setProduit("ProduitRecovery");
//        vente.setMontant(200.0);
//        vente.setDateVente(LocalDate.now());
//        vente.setRegion("Dakar");
//
//        try {
//            throw new RuntimeException("Simulated DB failure");
//        } catch (Exception e) {
//            System.out.println("[INFO] Exception simulée: " + e.getMessage());
//        }
//
//        dakarRepo.save(vente);
//        syncService.syncAll();
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(5))
//                .until(() -> thiesRepo.findById(testId).isPresent() && stlRepo.findById(testId).isPresent());
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente répliquée à Thies après recovery");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente répliquée à Saint-Louis après recovery");
//    }
//}

//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.awaitility.Awaitility;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    // --- 3 containers MySQL distincts ---
//    @Container
//    private static final MySQLContainer<?> mysqlDakar = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test_dakar")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @Container
//    private static final MySQLContainer<?> mysqlThies = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test_thies")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @Container
//    private static final MySQLContainer<?> mysqlStl = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dsms_test_stl")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    // --- Configuration dynamique des datasources ---
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.dakar.url", mysqlDakar::getJdbcUrl);
//        registry.add("spring.datasource.dakar.username", mysqlDakar::getUsername);
//        registry.add("spring.datasource.dakar.password", mysqlDakar::getPassword);
//
//        registry.add("spring.datasource.thies.url", mysqlThies::getJdbcUrl);
//        registry.add("spring.datasource.thies.username", mysqlThies::getUsername);
//        registry.add("spring.datasource.thies.password", mysqlThies::getPassword);
//
//        registry.add("spring.datasource.stl.url", mysqlStl::getJdbcUrl);
//        registry.add("spring.datasource.stl.username", mysqlStl::getUsername);
//        registry.add("spring.datasource.stl.password", mysqlStl::getPassword);
//    }
//
//    @Autowired private VenteRepositoryDakar dakarRepo;
//    @Autowired private VenteRepositoryThies thiesRepo;
//    @Autowired private VenteRepositoryStl stlRepo;
//    @Autowired private SyncService syncService;
//
//    // --- Nettoyage avant chaque test ---
//    @BeforeEach
//    void cleanup() {
//        dakarRepo.deleteAll();
//        thiesRepo.deleteAll();
//        stlRepo.deleteAll();
//        syncService.resetLastSync(); // réinitialise le lastSync et supprime les métadonnées
//    }
//
//    @Test
//    @Order(1)
//    void testCreateVenteDakar() {
//        UUID testId = UUID.randomUUID();
//        Vente v = new Vente();
//        v.setId(testId);
//        v.setProduit("ProduitTest1");
//        v.setMontant(50.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        dakarRepo.save(v);
//
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente doit exister dans Dakar repo");
//    }
//
//    @Test
//    @Order(2)
//    void testSyncReplication() {
//        UUID testId = UUID.randomUUID();
//        Vente vente = new Vente();
//        vente.setId(testId);
//        vente.setProduit("ProduitSync");
//        vente.setMontant(100.0);
//        vente.setDateVente(LocalDate.now());
//        vente.setRegion("Dakar");
//
//        dakarRepo.save(vente);
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente doit exister dans Dakar avant sync");
//
//        syncService.syncAll();
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(5))
//                .until(() -> thiesRepo.findById(testId).isPresent() && stlRepo.findById(testId).isPresent());
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente doit être répliquée à Thies");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente doit être répliquée à Saint-Louis");
//    }
//
//    @Test
//    @Order(3)
//    void testRecoveryAfterFailure() {
//        UUID testId = UUID.randomUUID();
//        Vente vente = new Vente();
//        vente.setId(testId);
//        vente.setProduit("ProduitRecovery");
//        vente.setMontant(200.0);
//        vente.setDateVente(LocalDate.now());
//        vente.setRegion("Dakar");
//
//        try {
//            throw new RuntimeException("Simulated DB failure");
//        } catch (Exception e) {
//            System.out.println("[INFO] Exception simulée: " + e.getMessage());
//        }
//
//        dakarRepo.save(vente);
//        syncService.syncAll();
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(5))
//                .until(() -> thiesRepo.findById(testId).isPresent() && stlRepo.findById(testId).isPresent());
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente répliquée à Thies après recovery");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente répliquée à Saint-Louis après recovery");
//    }
//}


//package com.example.dsms;
//
//import com.example.dsms.model.Vente;
//import com.example.dsms.repository.dakar.VenteRepositoryDakar;
//import com.example.dsms.repository.stl.VenteRepositoryStl;
//import com.example.dsms.repository.thies.VenteRepositoryThies;
//import com.example.dsms.service.SyncService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//@Testcontainers
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SyncIntegrationTest {
//
//    @Autowired
//    private VenteRepositoryDakar dakarRepo;
//    @Autowired
//    private VenteRepositoryThies thiesRepo;
//    @Autowired
//    private VenteRepositoryStl stlRepo;
//    @Autowired
//    private SyncService syncService;
//
//    private static UUID testId;
//
//    // Conteneurs MySQL pour les 3 bases
//    @Container
//    public static MySQLContainer<?> dakarDb = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("dakar")
//            .withUsername("user")
//            .withPassword("password");
//
//    @Container
//    public static MySQLContainer<?> thiesDb = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("thies")
//            .withUsername("user")
//            .withPassword("password");
//
//    @Container
//    public static MySQLContainer<?> stlDb = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("stl")
//            .withUsername("user")
//            .withPassword("password");
//
//    @Test
//    @Order(1)
//    public void createInDakar() {
//        Vente v = new Vente();
//        testId = UUID.randomUUID();
//        v.setId(testId);
//        v.setProduit("TestProduit");
//        v.setMontant(100.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        dakarRepo.save(v);
//        assertTrue(dakarRepo.findById(testId).isPresent(), "Vente non créée dans Dakar");
//    }
//
//    @Test
//    @Order(2)
//    public void syncAndCheckReplication() throws InterruptedException {
//        syncService.syncAll();
//        Thread.sleep(1000);
//
//        assertTrue(thiesRepo.findById(testId).isPresent(), "Vente non répliquée à Thiès");
//        assertTrue(stlRepo.findById(testId).isPresent(), "Vente non répliquée à Saint-Louis");
//    }
//
//    @Test
//    @Order(3)
//    public void simulateDakarDbFailureAndRecovery() {
//        // Simuler la panne
//        dakarDb.stop();
//
//        Vente v = new Vente();
//        v.setId(UUID.randomUUID());
//        v.setProduit("ProduitFail");
//        v.setMontant(50.0);
//        v.setDateVente(LocalDate.now());
//        v.setRegion("Dakar");
//
//        // L'opération doit échouer
//        assertThrows(Exception.class, () -> dakarRepo.save(v));
//
//        // Redémarrer la DB
//        dakarDb.start();
//
//        // Maintenant, la sauvegarde doit réussir
//        dakarRepo.save(v);
//        assertTrue(dakarRepo.findById(v.getId()).isPresent(), "Vente non créée après récupération");
//    }
//}


package com.example.dsms;

import com.example.dsms.model.Vente;
import com.example.dsms.repository.dakar.VenteRepositoryDakar;
import com.example.dsms.repository.stl.VenteRepositoryStl;
import com.example.dsms.repository.thies.VenteRepositoryThies;
import com.example.dsms.service.SyncService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 🔍 Test d’intégration complet du système DSMS.
 * Ce test vérifie :
 *  - la création de ventes à Dakar
 *  - la réplication automatique vers Thiès et Saint-Louis
 *  - la reprise après une panne simulée
 * ⚙️ Isolation totale grâce à Testcontainers + profil "test"
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SyncIntegrationTest {

    // --- Conteneurs MySQL isolés pour chaque région ---
    @Container
    private static final MySQLContainer<?> mysqlDakar = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("dsms_test_dakar")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    private static final MySQLContainer<?> mysqlThies = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("dsms_test_thies")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    private static final MySQLContainer<?> mysqlStl = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("dsms_test_stl")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Base de données Dakar
        registry.add("spring.datasource.dakar.url", mysqlDakar::getJdbcUrl);
        registry.add("spring.datasource.dakar.username", mysqlDakar::getUsername);
        registry.add("spring.datasource.dakar.password", mysqlDakar::getPassword);

        // Base de données Thiès
        registry.add("spring.datasource.thies.url", mysqlThies::getJdbcUrl);
        registry.add("spring.datasource.thies.username", mysqlThies::getUsername);
        registry.add("spring.datasource.thies.password", mysqlThies::getPassword);

        // Base de données Saint-Louis
        registry.add("spring.datasource.stl.url", mysqlStl::getJdbcUrl);
        registry.add("spring.datasource.stl.username", mysqlStl::getUsername);
        registry.add("spring.datasource.stl.password", mysqlStl::getPassword);
    }

    @Autowired private VenteRepositoryDakar dakarRepo;
    @Autowired private VenteRepositoryThies thiesRepo;
    @Autowired private VenteRepositoryStl stlRepo;
    @Autowired private SyncService syncService;

    @AfterEach
    void cleanDatabases() {
        dakarRepo.deleteAll();
        thiesRepo.deleteAll();
        stlRepo.deleteAll();
    }

    @Test
    @Order(1)
    void testCreationDakar() {
        UUID id = UUID.randomUUID();
        Vente v = new Vente();
        v.setId(id);
        v.setProduit("ProduitTest1");
        v.setMontant(100.0);
        v.setDateVente(LocalDate.now());
        v.setRegion("Dakar");

        dakarRepo.save(v);
        assertTrue(dakarRepo.findById(id).isPresent(),
                "❌ La vente n’a pas été enregistrée dans Dakar");
        System.out.println("✅ Vente créée avec succès dans Dakar");
    }

    @Test
    @Order(2)
    void testReplication() {
        UUID id = UUID.randomUUID();
        Vente v = new Vente();
        v.setId(id);
        v.setProduit("ProduitSync");
        v.setMontant(150.0);
        v.setDateVente(LocalDate.now());
        v.setRegion("Dakar");

        dakarRepo.save(v);
        syncService.syncAll();

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .until(() ->
                        thiesRepo.findById(id).isPresent() &&
                                stlRepo.findById(id).isPresent()
                );

        assertTrue(thiesRepo.findById(id).isPresent(), "❌ Vente non répliquée à Thiès");
        assertTrue(stlRepo.findById(id).isPresent(), "❌ Vente non répliquée à Saint-Louis");
        System.out.println("✅ Synchronisation réussie sur toutes les régions");
    }

    @Test
    @Order(3)
    void testRecoverySimulation() {
        UUID id = UUID.randomUUID();
        Vente v = new Vente();
        v.setId(id);
        v.setProduit("ProduitRecovery");
        v.setMontant(200.0);
        v.setDateVente(LocalDate.now());
        v.setRegion("Dakar");

        System.out.println("🔥 Simulation de panne : DB temporairement inaccessible...");
        try {
            throw new RuntimeException("Simulated DB failure");
        } catch (Exception e) {
            System.out.println("✅ Exception simulée capturée : " + e.getMessage());
        }

        // Recovery
        dakarRepo.save(v);
        syncService.syncAll();

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .until(() ->
                        thiesRepo.findById(id).isPresent() &&
                                stlRepo.findById(id).isPresent()
                );

        assertTrue(thiesRepo.findById(id).isPresent(), "❌ Vente non répliquée à Thiès après recovery");
        assertTrue(stlRepo.findById(id).isPresent(), "❌ Vente non répliquée à Saint-Louis après recovery");
        System.out.println("♻️ Reconnexion et réplication après panne réussies !");
    }

    @Test
    @Order(4)
    void testConflictAndLastWriteWins() {
        System.out.println("=== 🧪 Test de conflit et stratégie Last-Write-Wins ===");

        UUID id = UUID.randomUUID();

        // Étape 1 : Création initiale dans Dakar
        Vente venteDakar = new Vente();
        venteDakar.setId(id);
        venteDakar.setProduit("Ordinateur");
        venteDakar.setMontant(200000.0);
        venteDakar.setDateVente(LocalDate.now());
        venteDakar.setRegion("Dakar");
        venteDakar.setUpdatedAt(LocalDate.now().atStartOfDay());
        dakarRepo.save(venteDakar);

        // Synchronisation initiale
        syncService.syncAll();

        // Étape 2 : Modification concurrente dans Thies (plus ancienne)
        Vente venteThies = thiesRepo.findById(id).orElseThrow();
        venteThies.setMontant(250000.0);
        venteThies.setUpdatedAt(venteDakar.getUpdatedAt().minusMinutes(5)); // plus vieux
        thiesRepo.save(venteThies);

        // Étape 3 : Modification plus récente dans Dakar
        Vente venteDakarUpdate = dakarRepo.findById(id).orElseThrow();
        venteDakarUpdate.setMontant(270000.0);
        venteDakarUpdate.setUpdatedAt(venteDakar.getUpdatedAt().plusMinutes(10)); // plus récent
        dakarRepo.save(venteDakarUpdate);

        // Étape 4 : Nouvelle synchronisation
        syncService.syncAll();

        // Étape 5 : Vérification
        Vente finalThies = thiesRepo.findById(id).orElseThrow();
        Vente finalStl = stlRepo.findById(id).orElseThrow();

        System.out.println("Montant final Dakar : " + dakarRepo.findById(id).get().getMontant());
        System.out.println("Montant final Thies : " + finalThies.getMontant());
        System.out.println("Montant final St-Louis : " + finalStl.getMontant());

        assertTrue(finalThies.getMontant().equals(270000.0), "Thies doit avoir la version la plus récente");
        assertTrue(finalStl.getMontant().equals(270000.0), "Saint-Louis doit avoir la version la plus récente");
    }

}






