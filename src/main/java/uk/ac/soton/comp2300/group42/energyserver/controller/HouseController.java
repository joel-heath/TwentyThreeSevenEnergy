package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.energyserver.dto.HousemateResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
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
    public ResponseEntity<House> createHouse(@RequestBody House request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(houseService.createHouse(request, user));
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