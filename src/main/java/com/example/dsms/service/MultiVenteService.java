package com.example.dsms.service;

import com.example.dsms.model.Vente;
import com.example.dsms.repository.dakar.VenteRepositoryDakar;
import com.example.dsms.repository.stl.VenteRepositoryStl;
import com.example.dsms.repository.thies.VenteRepositoryThies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MultiVenteService {

    private final VenteRepositoryDakar dakarRepo;
    private final VenteRepositoryThies thiesRepo;
    private final VenteRepositoryStl stlRepo;

    public MultiVenteService(VenteRepositoryDakar dakarRepo,
                             VenteRepositoryThies thiesRepo,
                             VenteRepositoryStl stlRepo) {
        this.dakarRepo = dakarRepo;
        this.thiesRepo = thiesRepo;
        this.stlRepo = stlRepo;
    }

    @Transactional("dakarTransactionManager")
    public Vente saveOrUpdateToDakar(Vente v) {
        v.setRegion("Dakar");
        Optional<Vente> existing = dakarRepo.findById(v.getId());
        if (existing.isPresent()) {
            if (v.getUpdatedAt().isAfter(existing.get().getUpdatedAt())) {
                return dakarRepo.save(v);
            } else {
                return existing.get();
            }
        }
        return dakarRepo.save(v);
    }

    @Transactional("thiesTransactionManager")
    public Vente saveOrUpdateToThies(Vente v) {
        v.setRegion("Thies");
        Optional<Vente> existing = thiesRepo.findById(v.getId());
        if (existing.isPresent()) {
            if (v.getUpdatedAt().isAfter(existing.get().getUpdatedAt())) {
                return thiesRepo.save(v);
            } else {
                return existing.get();
            }
        }
        return thiesRepo.save(v);
    }

    @Transactional("stlTransactionManager")
    public Vente saveOrUpdateToStl(Vente v) {
        v.setRegion("Saint-Louis");
        Optional<Vente> existing = stlRepo.findById(v.getId());
        if (existing.isPresent()) {
            if (v.getUpdatedAt().isAfter(existing.get().getUpdatedAt())) {
                return stlRepo.save(v);
            } else {
                return existing.get();
            }
        }
        return stlRepo.save(v);
    }

    public void saveOrUpdateToAll(Vente v) {
        saveOrUpdateToDakar(v);
        saveOrUpdateToThies(v);
        saveOrUpdateToStl(v);
    }

    public List<Vente> findAllConsolidated() {
        List<Vente> all = new ArrayList<>();
        all.addAll(dakarRepo.findAll());
        all.addAll(thiesRepo.findAll());
        all.addAll(stlRepo.findAll());
        return all;
    }

    public List<Vente> findByRegion(String region) {
        return switch (region.toLowerCase()) {
            case "dakar" -> dakarRepo.findAll();
            case "thies" -> thiesRepo.findAll();
            case "saint-louis", "stl", "st-louis" -> stlRepo.findAll();
            default -> Collections.emptyList();
        };
    }
}
