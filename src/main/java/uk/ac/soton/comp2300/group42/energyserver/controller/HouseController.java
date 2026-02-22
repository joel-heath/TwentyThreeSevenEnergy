package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.HouseService;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@PreAuthorize("isAuthenticated()")
public class HouseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @PostMapping
    public ResponseEntity<HouseResponse> createHouse(@RequestBody CreateHouseRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(houseService.createHouse(request, user));
    }

    @PostMapping("/{houseId}")
    public ResponseEntity<HouseResponse> updateHouse(@RequestBody UpdateHouseRequest request, @PathVariable Long houseId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(houseService.updateHouse(request, houseId, user));
    }

    @GetMapping("/{houseId}/housemates")
    public ResponseEntity<List<HousemateResponse>> getHousemates(@PathVariable Long houseId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(houseService.getHousemates(houseId, user));
    }

    @GetMapping("/{houseId}/me")
    public ResponseEntity<HousemateResponse> getMyHousemateInfo(@PathVariable Long houseId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(houseService.getCurrentUserHousemate(houseId, user));
    }

}