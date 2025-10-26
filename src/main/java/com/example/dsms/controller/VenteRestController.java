package com.example.dsms.controller;

import com.example.dsms.model.Vente;
import com.example.dsms.service.MultiVenteService;
import com.example.dsms.service.SyncService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventes")
public class VenteRestController {

    private final MultiVenteService multiVenteService;
    private final SyncService syncService;

    public VenteRestController(MultiVenteService multiVenteService, SyncService syncService) {
        this.multiVenteService = multiVenteService;
        this.syncService = syncService;
    }

    // Enum pour les régions
    public enum Region {
        DAKAR, THIES, STL;

        public static Region fromString(String s) {
            return switch (s.toLowerCase()) {
                case "dakar" -> DAKAR;
                case "thies" -> THIES;
                case "stl", "saint-louis", "st-louis" -> STL;
                default -> null;
            };
        }
    }

    @GetMapping("/all")
    public List<Vente> getAll() {
        return multiVenteService.findAllConsolidated();
    }

    @PostMapping("/create/{region}")
    public ResponseEntity<?> create(@PathVariable String region, @Valid @RequestBody Vente vente) {
        Region r = Region.fromString(region);
        if (r == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Région invalide : " + region));
        }

        try {
            Vente saved = switch (r) {
                case DAKAR -> multiVenteService.saveOrUpdateToDakar(vente);
                case THIES -> multiVenteService.saveOrUpdateToThies(vente);
                case STL -> multiVenteService.saveOrUpdateToStl(vente);
            };
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> manualSync() {
        syncService.syncAll();
        return ResponseEntity.ok(Map.of("message", "Sync completed"));
    }

    @PostMapping("/sync-incremental")
    public ResponseEntity<Map<String, String>> manualIncrementalSync() {
        syncService.syncIncremental();
        return ResponseEntity.ok(Map.of("message", "Incremental sync completed"));
    }
}
