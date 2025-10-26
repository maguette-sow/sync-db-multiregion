package com.example.dsms.exception;

import com.example.dsms.model.Vente;
import com.example.dsms.service.MultiVenteService;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MultiVenteService multiVenteService;

    public GlobalExceptionHandler(MultiVenteService multiVenteService) {
        this.multiVenteService = multiVenteService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        model.addAttribute("validationErrors", errors);
        model.addAttribute("newVente", new Vente());
        model.addAttribute("ventes", multiVenteService.findAllConsolidated());
        return "index";
    }

    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("newVente", new Vente());
        model.addAttribute("ventes", multiVenteService.findAllConsolidated());
        return "index";
    }
}
