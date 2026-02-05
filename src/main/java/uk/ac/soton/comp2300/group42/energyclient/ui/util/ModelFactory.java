package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;

import java.util.HashMap;
import java.util.Map;

public class ModelFactory {
    private final Map<Integer, ApplianceModel> applianceCache = new HashMap<>();

    public ApplianceModel getApplianceModel(ApplianceDTO dto) {
        if (dto == null) return null;

        if (applianceCache.containsKey(dto.getId())) {
            ApplianceModel existingModel = applianceCache.get(dto.getId());
            if (!existingModel.getName().equals(dto.getName()))
                existingModel.setName(dto.getName());

            return applianceCache.get(dto.getId());
        }

        ApplianceModel newModel = new ApplianceModel(dto);
        applianceCache.put(dto.getId(), newModel);
        return newModel;
    }

    public ActivationModel createActivationModel(ActivationDTO dto) {
        ApplianceModel sharedAppliance = getApplianceModel(dto.getAppliance());

        return new ActivationModel(dto, sharedAppliance);
    }

    // Clear cache if user logs out or wants to do a full hard refresh
    public void clear() {
        applianceCache.clear();
    }
}