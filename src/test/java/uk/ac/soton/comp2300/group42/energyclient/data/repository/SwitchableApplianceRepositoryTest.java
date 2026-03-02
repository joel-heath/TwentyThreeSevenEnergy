package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchableApplianceRepositoryTest {

    @Mock Appliance dummyAppliance;

    @Mock LocalApplianceRepository localRepository;
    @Mock RemoteApplianceRepository remoteRepository;
    @Mock SessionManager sessionManager;

    @InjectMocks SwitchableApplianceRepository switchableRepository;

    @Test
    void add_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.add(dummyAppliance)).thenReturn(dummyAppliance);

        Appliance result = switchableRepository.add(dummyAppliance);

        assertEquals(dummyAppliance, result);
        verify(remoteRepository).add(dummyAppliance);
        verifyNoInteractions(localRepository);
    }

    @Test
    void add_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.add(dummyAppliance)).thenReturn(dummyAppliance);

        Appliance result = switchableRepository.add(dummyAppliance);

        assertEquals(dummyAppliance, result);
        verify(localRepository).add(dummyAppliance);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void get_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.get(100L, 1L)).thenReturn(dummyAppliance);

        Appliance result = switchableRepository.get(100L, 1L);

        assertEquals(dummyAppliance, result);
        verify(remoteRepository).get(100L, 1L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void get_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.get(100L, 1L)).thenReturn(dummyAppliance);

        Appliance result = switchableRepository.get(100L, 1L);

        assertEquals(dummyAppliance, result);
        verify(localRepository).get(100L, 1L);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void getAll_WhenLoggedIn_ShouldUseRemoteRepository() {
        List<Appliance> expectedList = List.of(dummyAppliance);
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getAll(100L)).thenReturn(expectedList);

        List<Appliance> result = switchableRepository.getAll(100L);

        assertEquals(expectedList, result);
        verify(remoteRepository).getAll(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void getAll_WhenNotLoggedIn_ShouldUseLocalRepository() {
        List<Appliance> expectedList = List.of(dummyAppliance);
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getAll(100L)).thenReturn(expectedList);

        List<Appliance> result = switchableRepository.getAll(100L);

        assertEquals(expectedList, result);
        verify(localRepository).getAll(100L);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void update_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.update(dummyAppliance)).thenReturn(dummyAppliance);

        Appliance result = switchableRepository.update(dummyAppliance);

        assertEquals(dummyAppliance, result);
        verify(remoteRepository).update(dummyAppliance);
        verifyNoInteractions(localRepository);
    }

    @Test
    void update_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.update(dummyAppliance)).thenReturn(dummyAppliance);

        Appliance result = switchableRepository.update(dummyAppliance);

        assertEquals(dummyAppliance, result);
        verify(localRepository).update(dummyAppliance);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void delete_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);

        switchableRepository.delete(100L, 1L);

        verify(remoteRepository).delete(100L, 1L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void delete_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);

        switchableRepository.delete(100L, 1L);

        verify(localRepository).delete(100L, 1L);
        verifyNoInteractions(remoteRepository);
    }
}