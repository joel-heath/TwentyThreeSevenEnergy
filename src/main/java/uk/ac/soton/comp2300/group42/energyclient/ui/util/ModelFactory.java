package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import uk.ac.soton.comp2300.group42.energyclient.data.api.Role;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.*;

import java.util.HashMap;
import java.util.Map;

public class ModelFactory {
    private final HousemateModel currentUser;
    private final PreferencesModel preferencesModel;
    private final Map<Long, ApplianceModel> applianceCache = new HashMap<>();
    private final Map<Long, ActivationModel> activationCache = new HashMap<>();
    private final Map<Long, HouseModel> houseCache = new HashMap<>();
    private final Map<Long, HousemateModel> housemateCache = new HashMap<>();

    public ModelFactory() {
        HouseDTO houseDTO = new HouseDTO(-1L, "Loading...", "Loading...");
        PreferencesDTO preferencesDTO = new PreferencesDTO();
        this.preferencesModel = new PreferencesModel(preferencesDTO, getHouseModel(houseDTO));

        HousemateDTO currentUserDTO = new HousemateDTO(-1L, "Loading...", "", "Loading...", -1L, Role.GUEST);
        currentUser = new HousemateModel(currentUserDTO, getHouseModel(houseDTO));
    }

    public HousemateModel getCurrentUser() { return currentUser; }
    public PreferencesModel getPreferencesModel() { return preferencesModel; }

    /**
     * Called when loading the Appliance List.
     * Updates existing models or creates new ones.
     */
    public ApplianceModel getApplianceModel(ApplianceDTO dto) {
        if (dto == null) return null;

        if (applianceCache.containsKey(dto.getId())) {
            ApplianceModel existing = applianceCache.get(dto.getId());
            existing.updateFrom(dto);
            return existing;
        }

        ApplianceModel newModel = new ApplianceModel(dto);
        applianceCache.put(dto.getId(), newModel);
        return newModel;
    }

    /**
     * Called when loading Activations.
     * Looks up the Appliance by ID.
     */
    public ActivationModel getActivationModel(ActivationDTO dto) {
        if (dto == null) return null;

        ApplianceModel appliance = applianceCache.get(dto.getApplianceId());

        // Handle orphaned activations (data integrity safety)
        if (appliance == null) {
            System.err.println("Orphaned Activation found for missing ApplianceID: " + dto.getApplianceId());
            // We could display an error or create a placeholder to avoid crashing the UI.
            ApplianceDTO placeholderDto = new ApplianceDTO(dto.getApplianceId(),"Unknown Device");
            appliance = getApplianceModel(placeholderDto);
        }

        if (activationCache.containsKey(dto.getId())) {
            ActivationModel existing = activationCache.get(dto.getId());
            existing.updateFrom(dto, appliance);
            return existing;
        }

        ActivationModel newModel = new ActivationModel(dto, appliance);
        activationCache.put(dto.getId(), newModel);
        return newModel;
    }

    /**
     * Called when loading the House List.
     * Updates existing models or creates new ones.
     */
    public HouseModel getHouseModel(HouseDTO dto) {
        if (dto == null) return null;

        if (houseCache.containsKey(dto.getId())) {
            HouseModel existing = houseCache.get(dto.getId());
            existing.updateFrom(dto);
            return existing;
        }

        HouseModel newModel = new HouseModel(dto);
        houseCache.put(dto.getId(), newModel);
        return newModel;
    }

    /**
     * Called when loading Housemates.
     * Looks up the House by ID.
     */
    public HousemateModel getHousemateModel(HousemateDTO dto) {
        if (dto == null) return null;

        HouseModel house = houseCache.get(dto.getHouseId());

        // Handle orphaned activations (data integrity safety)
        if (house == null) {
            System.err.println("Orphaned Housemate found for missing HouseID: " + dto.getHouseId());
            // We could display an error or create a placeholder to avoid crashing the UI.
            HouseDTO placeholderDto = new HouseDTO(dto.getHouseId(), "Unknown House", "");
            house = getHouseModel(placeholderDto);
        }

        if (housemateCache.containsKey(dto.getId())) {
            HousemateModel existing = housemateCache.get(dto.getId());
            existing.updateFrom(dto, house);
            return existing;
        }

        HousemateModel newModel = new HousemateModel(dto, house);
        housemateCache.put(dto.getId(), newModel);
        return newModel;
    }

    // Clear cache if user logs out or wants to do a full hard refresh
    public void clear() {
        applianceCache.clear();
    }
}