package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.energyprice.EnergyPriceResponse;
import uk.ac.soton.comp2300.group42.energyprice.SaveEnergyPriceRequest;
import uk.ac.soton.comp2300.group42.energyserver.service.EnergyPriceService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/energy-prices")
@PreAuthorize("isAuthenticated()")
public class EnergyPriceController {

    private final EnergyPriceService service;

    public EnergyPriceController(EnergyPriceService service) {
        this.service = service;
    }

    @PostMapping("/prices")
    public ResponseEntity<List<EnergyPriceResponse>> savePrices(
            @RequestBody List<SaveEnergyPriceRequest> requests) {
        return ResponseEntity.ok(service.saveAll(requests));
    }

    @GetMapping("/prices")
    public ResponseEntity<List<EnergyPriceResponse>> getPrices(
            @RequestParam(required = false) LocalDate date) {
        if (date != null) {
            return ResponseEntity.ok(service.getPricesForDate(date));
        }
        return ResponseEntity.ok(service.getAllPrices());
    }
}
