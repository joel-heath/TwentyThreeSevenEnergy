package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.MetricsService;
import uk.ac.soton.comp2300.group42.metrics.MetricsResponse;


@RestController
@RequestMapping("/api/houses/{houseId}/metrics")
@PreAuthorize("isAuthenticated()")
public class MetricsController {

    private final MetricsService service;

    public MetricsController(MetricsService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricsResponse> getMetricsForHouse(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getMetricsById(houseId, id, user));
    }

}
