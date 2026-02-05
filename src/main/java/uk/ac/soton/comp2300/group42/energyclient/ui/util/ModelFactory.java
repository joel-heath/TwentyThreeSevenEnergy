package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;

import java.util.HashMap;
import java.util.Map;

public class ModelFactory {
    private final Map<Long, ApplianceModel> applianceCache = new HashMap<>();

    public ApplianceModel getApplianceModel(ApplianceDTO dto) {
        if (dto == null) return null;

        if (applianceCache.containsKey(dto.getId())) {
            ApplianceModel existingModel = applianceCache.get(dto.getId());
            if (!existingModel.getName().equals(dto.getName())) {
                existingModel.setName(dto.getName());
            }
            return existingModel;
        }

        ApplianceModel newModel = new ApplianceModel(dto);
        applianceCache.put(dto.getId(), newModel);
        return newModel;
    }

    // Assumes cache is populated (the ViewModel caller has run
    public ApplianceModel getApplianceModelById(Long id) {
        return applianceCache.get(id);
    }

    // Assumes cache is populated (the ViewModel caller has run
    public ActivationModel createActivationModel(ActivationDTO dto) {
        Long applianceId = dto.getApplianceId();
        ApplianceModel sharedAppliance = getApplianceModelById(applianceId);

        if (sharedAppliance == null) {
            System.err.println("Warning: Activation found for unknown Appliance ID: " + applianceId);
            return null;
        }

        return new ActivationModel(dto, sharedAppliance);
    }

    // Clear cache if user logs out or wants to do a full hard refresh
    public void clear() {
        applianceCache.clear();
    }
}