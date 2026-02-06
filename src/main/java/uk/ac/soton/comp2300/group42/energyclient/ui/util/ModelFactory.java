package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;

import java.util.HashMap;
import java.util.Map;

public class ModelFactory {
    private PreferencesModel preferencesModel;
    private final Map<Long, ApplianceModel> applianceCache = new HashMap<>();
    private final Map<Long, ActivationModel> activationCache = new HashMap<>();

    public PreferencesModel getPreferencesModel(PreferencesDTO dto) {
        if (preferencesModel != null)
            preferencesModel.updateFrom(dto);
        else
            preferencesModel = new PreferencesModel(dto);

        return preferencesModel;
    }

    /**
     * Called when loading the Appliance List.
     * Updates existing models or creates new ones.
     */
    public ApplianceModel saveAppliance(ApplianceDTO dto) {
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
    public ActivationModel createActivationModel(ActivationDTO dto) {
        if (dto == null) return null;

        ApplianceModel appliance = applianceCache.get(dto.getApplianceId());

        // Handle orphaned activations (data integrity safety)
        if (appliance == null) {
            System.err.println("Orphaned Activation found for missing ApplianceID: " + dto.getApplianceId());
            // We could display an error or create a placeholder to avoid crashing the UI.
            ApplianceDTO placeholderDto = new ApplianceDTO(
                    dto.getApplianceId(),
                    "Unknown Device");
            appliance = saveAppliance(placeholderDto);
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

    // Clear cache if user logs out or wants to do a full hard refresh
    public void clear() {
        applianceCache.clear();
    }
}