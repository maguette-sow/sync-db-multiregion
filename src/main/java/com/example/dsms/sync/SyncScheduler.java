package com.example.dsms.sync;

import com.example.dsms.model.Vente;
import com.example.dsms.service.MultiVenteService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class SyncScheduler {

    private final MultiVenteService multiVenteService;

    // Dernière date de synchronisation par région
    private LocalDateTime lastSyncDakar = LocalDateTime.MIN;
    private LocalDateTime lastSyncThies = LocalDateTime.MIN;
    private LocalDateTime lastSyncStl   = LocalDateTime.MIN;

    public SyncScheduler(MultiVenteService multiVenteService) {
        this.multiVenteService = multiVenteService;
    }

    /**
     * Synchronisation incrémentale toutes les minutes
     */
    @Scheduled(fixedRate = 60_000)
    public void syncIncremental() {
        System.out.println("[SyncScheduler] Starting incremental synchronization...");

        syncRegionIncremental("dakar", lastSyncDakar);
        syncRegionIncremental("thies", lastSyncThies);
        syncRegionIncremental("stl", lastSyncStl);

        System.out.println("[SyncScheduler] Incremental synchronization completed.");
    }

    /**
     * Synchronise les ventes d'une région vers les autres régions
     * uniquement celles créées ou mises à jour depuis la dernière synchronisation
     */
    private void syncRegionIncremental(String sourceRegion, LocalDateTime lastSyncTime) {
        List<Vente> sourceVentes = multiVenteService.findByRegion(sourceRegion).stream()
                .filter(v -> v.getUpdatedAt() != null && v.getUpdatedAt().isAfter(lastSyncTime))
                .collect(Collectors.toList());

        // Déterminer les régions cibles
        List<String> targets = Arrays.asList("dakar", "thies", "stl").stream()
                .filter(r -> !r.equalsIgnoreCase(sourceRegion))
                .collect(Collectors.toList());

        for (String targetRegion : targets) {
            for (Vente v : sourceVentes) {
                Vente copy = new Vente();
                copy.setId(v.getId());
                copy.setProduit(v.getProduit());
                copy.setMontant(v.getMontant());
                copy.setDateVente(v.getDateVente());
                copy.setUpdatedAt(v.getUpdatedAt());

                switch (targetRegion.toLowerCase()) {
                    case "dakar" -> multiVenteService.saveOrUpdateToDakar(copy);
                    case "thies" -> multiVenteService.saveOrUpdateToThies(copy);
                    case "stl"   -> multiVenteService.saveOrUpdateToStl(copy);
                }
                System.out.printf("[SyncScheduler] Copied vente %s from %s to %s%n", v.getId(), sourceRegion, targetRegion);
            }
        }

        // Mettre à jour la dernière date de sync
        LocalDateTime maxUpdated = sourceVentes.stream()
                .map(Vente::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(lastSyncTime);

        switch (sourceRegion.toLowerCase()) {
            case "dakar" -> lastSyncDakar = maxUpdated;
            case "thies" -> lastSyncThies = maxUpdated;
            case "stl"   -> lastSyncStl = maxUpdated;
        }
    }
}
