package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.CreateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.UpdateActivationRequest;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;

@Mapper
public interface ActivationMapper {
    Activation toActivation(ActivationResponse response);
    CreateActivationRequest toCreateActivationRequest(Activation activation);
    UpdateActivationRequest toUpdateActivationRequest(Activation activation);
}
