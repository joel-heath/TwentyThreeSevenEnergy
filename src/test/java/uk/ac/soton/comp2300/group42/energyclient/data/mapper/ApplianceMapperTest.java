package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.appliance.CreateApplianceRequest;
import uk.ac.soton.comp2300.group42.appliance.UpdateApplianceRequest;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;

import static org.junit.jupiter.api.Assertions.*;

class ApplianceMapperTest {

    private final ApplianceMapper mapper = Mappers.getMapper(ApplianceMapper.class);

    @Test
    void toAppliance_ShouldMapAllFieldsCorrectly() {
        ApplianceResponse response = new ApplianceResponse(1L, 2L, "Washing Machine");

        Appliance domain = mapper.toAppliance(response);

        assertNotNull(domain);
        assertEquals(1L, domain.id());
        assertEquals(2L, domain.houseId());
        assertEquals("Washing Machine", domain.name());
    }

    @Test
    void toAppliance_ShouldReturnNullWhenInputIsNull() {
        Appliance domain = mapper.toAppliance(null);

        assertNull(domain);
    }

    @Test
    void toCreateApplianceRequest_ShouldMapNameAndDropIds() {
        Appliance domain = new Appliance(1L, 2L, "Washing Machine");

        CreateApplianceRequest request = mapper.toCreateApplianceRequest(domain);

        assertNotNull(request);
        assertEquals("Washing Machine", request.name());
    }

    @Test
    void toCreateApplianceRequest_ShouldReturnNullWhenInputIsNull() {
        CreateApplianceRequest request = mapper.toCreateApplianceRequest(null);

        assertNull(request);
    }

    @Test
    void toUpdateApplianceRequest_ShouldMapNameAndDropIds() {
        Appliance domain = new Appliance(1L, 2L, "Washing Machine");

        UpdateApplianceRequest request = mapper.toUpdateApplianceRequest(domain);

        assertNotNull(request);
        assertEquals("Washing Machine", request.name());
    }

    @Test
    void toUpdateApplianceRequest_ShouldReturnNullWhenInputIsNull() {
        UpdateApplianceRequest request = mapper.toUpdateApplianceRequest(null);

        assertNull(request);
    }
}