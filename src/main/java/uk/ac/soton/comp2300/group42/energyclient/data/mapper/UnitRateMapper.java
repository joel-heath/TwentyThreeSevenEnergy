package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.energyclient.data.external.UnitRateResponse;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;

@Mapper
public interface UnitRateMapper {
    UnitRate toUnitRate(UnitRateResponse response);
}
