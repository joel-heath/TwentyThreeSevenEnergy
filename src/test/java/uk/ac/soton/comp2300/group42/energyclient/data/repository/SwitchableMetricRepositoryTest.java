package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Metric;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchableMetricRepositoryTest {

    @Mock Metric dummyMetric;

    @Mock LocalMetricRepository localRepository;
    @Mock RemoteMetricRepository remoteRepository;
    @Mock SessionManager sessionManager;

    @InjectMocks SwitchableMetricRepository switchableRepository;

    @Test
    void add_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.add(dummyMetric)).thenReturn(dummyMetric);

        Metric result = switchableRepository.add(dummyMetric);

        assertEquals(dummyMetric, result);
        verify(remoteRepository).add(dummyMetric);
        verifyNoInteractions(localRepository);
    }

    @Test
    void add_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.add(dummyMetric)).thenReturn(dummyMetric);

        Metric result = switchableRepository.add(dummyMetric);

        assertEquals(dummyMetric, result);
        verify(localRepository).add(dummyMetric);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void get_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.get(100L, 1L)).thenReturn(dummyMetric);

        Metric result = switchableRepository.get(100L, 1L);

        assertEquals(dummyMetric, result);
        verify(remoteRepository).get(100L, 1L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void get_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.get(100L, 1L)).thenReturn(dummyMetric);

        Metric result = switchableRepository.get(100L, 1L);

        assertEquals(dummyMetric, result);
        verify(localRepository).get(100L, 1L);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void getAll_WhenLoggedIn_ShouldUseRemoteRepository() {
        List<Metric> expectedList = List.of(dummyMetric);
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getAll(100L)).thenReturn(expectedList);

        List<Metric> result = switchableRepository.getAll(100L);

        assertEquals(expectedList, result);
        verify(remoteRepository).getAll(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void getAll_WhenNotLoggedIn_ShouldUseLocalRepository() {
        List<Metric> expectedList = List.of(dummyMetric);
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getAll(100L)).thenReturn(expectedList);

        List<Metric> result = switchableRepository.getAll(100L);

        assertEquals(expectedList, result);
        verify(localRepository).getAll(100L);
        verifyNoInteractions(remoteRepository);
    }
}