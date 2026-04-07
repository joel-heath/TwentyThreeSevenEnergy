package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.energyprice.EnergyCostResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.EnergyPriceService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/houses")
@PreAuthorize("isAuthenticated()")
public class EnergyCostController {

    private final EnergyPriceService service;

    public EnergyCostController(EnergyPriceService service) {
        this.service = service;
    }

    @GetMapping("/{houseId}/costs")
    public ResponseEntity<List<EnergyCostResponse>> getHouseCosts(
            @PathVariable Long houseId,
            @RequestParam LocalDate date,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getDailyBreakdown(houseId, date, user));
    }
}
