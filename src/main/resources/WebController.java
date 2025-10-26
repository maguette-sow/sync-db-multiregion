package com.example.dsms.controller;

import com.example.dsms.model.Vente;
import com.example.dsms.service.VenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class WebController {

    private final VenteService service;

    public WebController(VenteService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("ventes", service.findAll());
        model.addAttribute("total", service.totalGlobal());
        return "index";
    }

    @PostMapping("/ventes/add")
    public String addVente(@RequestParam String produit,
                           @RequestParam Double montant,
                           @RequestParam String region) {
        Vente v = new Vente();
        v.setProduit(produit);
        v.setMontant(montant);
        v.setDateVente(LocalDate.now());
        v.setRegion(region);
        service.save(v);
        return "redirect:/";
    }
}
