package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.appliance.CreateApplianceRequest;
import uk.ac.soton.comp2300.group42.appliance.UpdateApplianceRequest;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;

@Mapper
public interface ApplianceMapper {
    Appliance toAppliance(ApplianceResponse response);
    CreateApplianceRequest toCreateApplianceRequest(Appliance appliance);
    UpdateApplianceRequest toUpdateApplianceRequest(Appliance appliance);
}
