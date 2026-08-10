package uk.ac.soton.comp2300.group42.energyserver.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.CreateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.UpdateActivationRequest;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.ActivationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/houses/{houseId}/activations")
@PreAuthorize("isAuthenticated()")
public class ActivationController {

    private final ActivationService service;

    public ActivationController(ActivationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ActivationResponse> createActivation(
            @PathVariable Long houseId,
            @Valid @RequestBody CreateActivationRequest request,
            @AuthenticationPrincipal User user) {
        ActivationResponse response = service.createActivation(houseId, request, user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivationResponse> getActivation(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getActivationById(houseId, id, user));
    }

    @GetMapping
    public ResponseEntity<List<ActivationResponse>> getAllActivations(
            @PathVariable Long houseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getActivationsByHouseId(houseId, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivationResponse> updateActivation(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateActivationRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.updateActivation(houseId, id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivation(
            @PathVariable Long houseId,
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        service.deleteActivation(houseId, id, user);
        return ResponseEntity.noContent().build();
    }
}