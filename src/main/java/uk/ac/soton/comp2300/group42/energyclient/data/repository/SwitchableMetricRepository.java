package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.time.LocalDate;
import java.util.List;

@Singleton
public class SwitchableMetricRepository implements MetricRepository {

    private final LocalMetricRepository localRepository;
    private final RemoteMetricRepository remoteRepository;
    private final SessionManager sessionManager;

    @Inject
    public SwitchableMetricRepository(LocalMetricRepository localRepository, RemoteMetricRepository remoteRepository, SessionManager sessionManager) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
        this.sessionManager = sessionManager;
    }

    private MetricRepository getActiveRepo() {
        return sessionManager.isLoggedIn() ? remoteRepository : localRepository;
    }

    @Override
    public Metric add(Metric metric, EnergyCategory category) {
        return getActiveRepo().add(metric, category);
    }

    @Override
    public Metric get(Long houseId, Long metricId) {
        return getActiveRepo().get(houseId, metricId);
    }

    @Override
    public List<Metric> getAll(Long houseId) {
        return getActiveRepo().getAll(houseId);
    }

    @Override
    public List<Metric> getAllByDate(Long houseId, LocalDate date) {
        return getActiveRepo().getAllByDate(houseId, date);
    }

    @Override
    public List<Metric> getAllByCategory(Long houseId, EnergyCategory category) {
        return getActiveRepo().getAllByCategory(houseId, category);
    }
}
