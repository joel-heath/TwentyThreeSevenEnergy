package uk.ac.soton.comp2300.group42.energyserver.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.MetricService;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;
import uk.ac.soton.comp2300.group42.metric.SaveMetricRequest;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/houses/{houseId}/metrics")
@PreAuthorize("isAuthenticated()")
public class MetricController {

    private final MetricService service;

    public MetricController(MetricService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricResponse> getMetric(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getMetricById(houseId, id, user)); // use house id for now
    }

    @PostMapping("/seed-test-data")
    public ResponseEntity<MetricResponse> seedData(
            @PathVariable Long houseId,
            @Valid @RequestBody SaveMetricRequest request,
            @AuthenticationPrincipal User user) {

        LocalDate testDate = LocalDate.now();
        Double testEnergyUsed = 1.0;
        return ResponseEntity.ok(service.saveTestMetric(houseId, testDate, testEnergyUsed, request, user));
    }
}
