package com.example.dsms.service;

import com.example.dsms.model.SyncMetadata;
import com.example.dsms.model.Vente;
import com.example.dsms.repository.dakar.SyncMetadataRepository;
import com.example.dsms.repository.dakar.VenteRepositoryDakar;
import com.example.dsms.repository.stl.VenteRepositoryStl;
import com.example.dsms.repository.thies.VenteRepositoryThies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final VenteRepositoryDakar dakarRepo;
    private final VenteRepositoryThies thiesRepo;
    private final VenteRepositoryStl stlRepo;
    private final SyncMetadataRepository syncMetaRepo;
    private final MultiVenteService multiVenteService;

    private final AtomicReference<LocalDateTime> lastSync = new AtomicReference<>(LocalDateTime.of(1970, 1, 1, 0, 0));

    public SyncService(VenteRepositoryDakar dakarRepo,
                       VenteRepositoryThies thiesRepo,
                       VenteRepositoryStl stlRepo,
                       SyncMetadataRepository syncMetaRepo,
                       MultiVenteService multiVenteService) {
        this.dakarRepo = dakarRepo;
        this.thiesRepo = thiesRepo;
        this.stlRepo = stlRepo;
        this.syncMetaRepo = syncMetaRepo;
        this.multiVenteService = multiVenteService;
        initLastSync();
    }

    private void initLastSync() {
        syncMetaRepo.findById(1L).ifPresentOrElse(
                meta -> lastSync.set(safeDate(meta.getLastSync())),
                () -> lastSync.set(LocalDateTime.of(1970, 1, 1, 0, 0))
        );
        log.info("Dernier timestamp de sync : {}", lastSync.get());
    }

    private LocalDateTime safeDate(LocalDateTime dt) {
        if (dt == null || dt.getYear() < 1970) {
            return LocalDateTime.of(1970, 1, 1, 0, 0);
        }
        return dt;
    }

    @Scheduled(fixedDelayString = "${sync.interval-ms:60000}")
    public void scheduledSync() {
        try {
            syncIncremental();
        } catch (Exception e) {
            log.error("Erreur pendant la synchronisation :", e);
        }
    }

    public void syncIncremental() {
        LocalDateTime since = safeDate(lastSync.get());
        log.info("Démarrage synchronisation incrémentale depuis {}", since);

        List<Vente> dakar = dakarRepo.findByUpdatedAtAfter(since);
        List<Vente> thies = thiesRepo.findByUpdatedAtAfter(since);
        List<Vente> stl = stlRepo.findByUpdatedAtAfter(since);

        Map<UUID, Vente> latest = new HashMap<>();
        Stream.of(dakar, thies, stl)
                .flatMap(Collection::stream)
                .forEach(v -> latest.merge(v.getId(), v, (oldV, newV) ->
                        newV.getUpdatedAt().isAfter(oldV.getUpdatedAt()) ? newV : oldV));

        latest.values().forEach(multiVenteService::saveOrUpdateToAll);

        LocalDateTime maxUpdated = latest.values().stream()
                .map(Vente::getUpdatedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        lastSync.set(maxUpdated);
        syncMetaRepo.save(new SyncMetadata(maxUpdated));

        log.info("Synchronisation incrémentale terminée. lastSync = {}", maxUpdated);
    }

    public void syncAll() {
        log.info("Démarrage synchronisation complète");

        List<Vente> all = Stream.of(dakarRepo.findAll(), thiesRepo.findAll(), stlRepo.findAll())
                .flatMap(Collection::stream)
                .toList();

        Map<UUID, Vente> latest = new HashMap<>();
        all.forEach(v -> latest.merge(v.getId(), v, (oldV, newV) ->
                newV.getUpdatedAt().isAfter(oldV.getUpdatedAt()) ? newV : oldV));

        latest.values().forEach(multiVenteService::saveOrUpdateToAll);

        LocalDateTime now = LocalDateTime.now();
        lastSync.set(now);
        syncMetaRepo.save(new SyncMetadata(now));

        log.info("Synchronisation complète terminée. lastSync = {}", now);
    }
}
