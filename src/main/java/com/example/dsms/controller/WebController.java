package com.example.dsms.controller;

import com.example.dsms.model.Vente;
import com.example.dsms.service.MultiVenteService;
import com.example.dsms.service.SyncService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web")
public class WebController {

    private final MultiVenteService multiVenteService;
    private final SyncService syncService;

    public WebController(MultiVenteService multiVenteService, SyncService syncService) {
        this.multiVenteService = multiVenteService;
        this.syncService = syncService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Vente> ventes = multiVenteService.findAllConsolidated();
        model.addAttribute("ventes", ventes);
        model.addAttribute("newVente", new Vente());
        return "index";
    }

    @PostMapping("/add")
    public String addVente(@ModelAttribute("newVente") @Valid Vente vente,
                           @RequestParam("region") String region,
                           Model model) {

        try {
            switch (region.toLowerCase()) {
                case "dakar" -> multiVenteService.saveOrUpdateToDakar(vente);
                case "thies" -> multiVenteService.saveOrUpdateToThies(vente);
                case "saint-louis", "stl", "st-louis" -> multiVenteService.saveOrUpdateToStl(vente);
                default -> throw new IllegalArgumentException("Région invalide : " + region);
            }
            model.addAttribute("success", "Vente ajoutée avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        // Important : on recharge la liste ici avant d'afficher la page
        model.addAttribute("ventes", multiVenteService.findAllConsolidated());
        model.addAttribute("newVente", new Vente());
        return "index";
    }

    @PostMapping("/sync-manual")
    public String manualSync(Model model) {
        syncService.syncIncremental();
        model.addAttribute("ventes", multiVenteService.findAllConsolidated());
        model.addAttribute("newVente", new Vente());
        model.addAttribute("success", "Synchronisation effectuée !");
        return "index";
    }
}
