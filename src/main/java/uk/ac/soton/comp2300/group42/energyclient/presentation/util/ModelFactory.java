package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.*;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ModelFactory {
    private final HousemateModel currentUser;
    private final PreferencesModel preferencesModel;
    private final Map<Long, ApplianceModel> applianceCache = new HashMap<>();
    private final Map<Long, ActivationModel> activationCache = new HashMap<>();
    private final Map<Long, HouseModel> houseCache = new HashMap<>();
    private final Map<Long, HousemateModel> housemateCache = new HashMap<>();

    public ModelFactory() {
        House house = new House(-1L, "Loading...", "Loading...", ZoneId.systemDefault(), Role.GUEST);
        Preferences preferences = new Preferences();
        this.preferencesModel = new PreferencesModel(preferences, getHouseModel(house));

        Housemate currentUser = new Housemate(-1L, -1L, "Loading...", "Loading...", Role.GUEST);
        this.currentUser = new HousemateModel(currentUser, getHouseModel(house));
    }

    public HousemateModel getCurrentUser() { return currentUser; }
    public PreferencesModel getPreferencesModel() { return preferencesModel; }
    public HouseModel getActiveHouse() { return preferencesModel.getActiveHouse(); }

    /**
     * Called when loading the Appliance List.
     * Updates existing models or creates new ones.
     */
    public ApplianceModel getApplianceModel(Appliance domainModel) {
        if (domainModel == null) return null;

        if (applianceCache.containsKey(domainModel.id())) {
            ApplianceModel existing = applianceCache.get(domainModel.id());
            existing.updateFrom(domainModel);
            return existing;
        }

        ApplianceModel viewModel = new ApplianceModel(domainModel, getActiveHouse());
        applianceCache.put(domainModel.id(), viewModel);
        return viewModel;
    }

    /**
     * Called when loading Activations.
     * Looks up the Appliance by ID.
     */
    public ActivationModel getActivationModel(Activation domainModel) {
        if (domainModel == null) return null;

        ApplianceModel appliance = applianceCache.get(domainModel.applianceId());

        // db currently cascade deletes Activations, so orphaned Activations should not be possible
        if (appliance == null) {
            System.err.println("Orphaned Activation found for missing ApplianceID: " + domainModel.applianceId());
            Appliance placeholderDto = new Appliance(domainModel.applianceId(), getActiveHouse().getId(), "Unknown Appliance");
            appliance = getApplianceModel(placeholderDto);
        }

        if (activationCache.containsKey(domainModel.id())) {
            ActivationModel existing = activationCache.get(domainModel.id());
            existing.updateFrom(domainModel, appliance);
            return existing;
        }

        ActivationModel viewModel = new ActivationModel(domainModel, appliance);
        activationCache.put(domainModel.id(), viewModel);
        return viewModel;
    }

    /**
     * Called when loading the House List.
     * Updates existing models or creates new ones.
     */
    public HouseModel getHouseModel(House domainModel) {
        if (domainModel == null) return null;

        if (houseCache.containsKey(domainModel.id())) {
            HouseModel existing = houseCache.get(domainModel.id());
            existing.updateFrom(domainModel);
            return existing;
        }

        HouseModel viewModel = new HouseModel(domainModel);
        houseCache.put(domainModel.id(), viewModel);
        return viewModel;
    }

    /**
     * Called when loading Housemates.
     * Looks up the House by ID.
     */
    public HousemateModel getHousemateModel(Housemate domainModel) {
        if (domainModel == null) return null;

        HouseModel house = houseCache.get(domainModel.houseId());

        // orphaned housemates should also not be possible
        if (house == null) {
            System.err.println("Orphaned Housemate found for missing HouseID: " + domainModel.houseId());
            House placeholderDto = new House(domainModel.houseId(), "Unknown House", "Unknown Address", ZoneId.systemDefault(), Role.GUEST);
            house = getHouseModel(placeholderDto);
        }

        if (housemateCache.containsKey(domainModel.userId())) {
            HousemateModel existing = housemateCache.get(domainModel.userId());
            existing.updateFrom(domainModel, house);
            return existing;
        }

        HousemateModel newModel = new HousemateModel(domainModel, house);
        housemateCache.put(domainModel.userId(), newModel);
        return newModel;
    }

    // Clear cache if user logs out or wants to do a full hard refresh
    public void clear() {
        applianceCache.clear();
    }
}