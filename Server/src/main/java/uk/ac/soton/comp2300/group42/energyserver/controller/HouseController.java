package uk.ac.soton.comp2300.group42.energyserver.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.HouseService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/houses")
@PreAuthorize("isAuthenticated()")
public class HouseController {

    private final HouseService service;

    public HouseController(HouseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<HouseResponse> createHouse(
            @Valid @RequestBody CreateHouseRequest request,
            @AuthenticationPrincipal User user) {
        HouseResponse response = service.createHouse(request, user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{houseId}")
    public ResponseEntity<HouseResponse> getHouse(
            @PathVariable Long houseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getHouseById(houseId, user));
    }

    @PutMapping("/{houseId}")
    public ResponseEntity<HouseResponse> updateHouse(
            @PathVariable Long houseId,
            @Valid @RequestBody UpdateHouseRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.updateHouse(houseId, request, user));
    }

    @DeleteMapping("/{houseId}")
    public ResponseEntity<Void> deleteHouse(
            @PathVariable Long houseId,
            @AuthenticationPrincipal User user) {
        service.deleteHouse(houseId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<HouseResponse>> getMyHouses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getHousesByUser(user));
    }

    @GetMapping("/{houseId}/me")
    public ResponseEntity<HousemateResponse> getMyHousemateInfo(
            @PathVariable Long houseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getUserAsHousemate(houseId, user));
    }

    @GetMapping("/{houseId}/housemates")
    public ResponseEntity<List<HousemateResponse>> getHousemates(
            @PathVariable Long houseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getHousemates(houseId, user));
    }
}