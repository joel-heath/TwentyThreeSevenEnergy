package uk.ac.soton.comp2300.group42.energyserver.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.appliance.CreateApplianceRequest;
import uk.ac.soton.comp2300.group42.appliance.UpdateApplianceRequest;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.ApplianceService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/houses/{houseId}/appliances")
@PreAuthorize("isAuthenticated()")
public class ApplianceController {

    private final ApplianceService service;

    public ApplianceController(ApplianceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApplianceResponse> createAppliance(
            @PathVariable Long houseId,
            @Valid @RequestBody CreateApplianceRequest request,
            @AuthenticationPrincipal User user) {
        ApplianceResponse response = service.createAppliance(houseId, request, user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplianceResponse> getAppliance(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getApplianceById(houseId, id, user));
    }

    @GetMapping
    public ResponseEntity<List<ApplianceResponse>> getAllAppliances(
            @PathVariable Long houseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getAppliancesByHouseId(houseId, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplianceResponse> updateAppliance(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplianceRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.updateAppliance(houseId, id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppliance(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        service.deleteAppliance(houseId, id, user);
        return ResponseEntity.noContent().build();
    }
}