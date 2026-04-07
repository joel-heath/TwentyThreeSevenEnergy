package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyCost;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyPrice;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.UnitRate;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class SwitchableEnergyPriceRepository implements EnergyPriceRepository {

    private final LocalEnergyPriceRepository localRepository;
    private final RemoteEnergyPriceRepository remoteRepository;
    private final SessionManager sessionManager;

    @Inject
    public SwitchableEnergyPriceRepository(LocalEnergyPriceRepository localRepository, RemoteEnergyPriceRepository remoteRepository, SessionManager sessionManager) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
        this.sessionManager = sessionManager;
    }

    private EnergyPriceRepository getActiveRepo() {
        return sessionManager.isLoggedIn() ? remoteRepository : localRepository;
    }

    @Override
    public List<EnergyPrice> syncAndGetNext24Hours() {
        return getActiveRepo().syncAndGetNext24Hours();
    }

    @Override
    public List<EnergyPrice> getPricesForDate(LocalDate date) {
        return getActiveRepo().getPricesForDate(date);
    }

    @Override
    public List<EnergyPrice> getAllPrices() {
        return getActiveRepo().getAllPrices();
    }

    @Override
    public List<UnitRate> fetchNext12Hours() {
        return getActiveRepo().fetchNext12Hours();
    }

    @Override
    public List<EnergyCost> getCostsForDate(Long houseId, LocalDate date) {
        return getActiveRepo().getCostsForDate(houseId, date);
    }
}
