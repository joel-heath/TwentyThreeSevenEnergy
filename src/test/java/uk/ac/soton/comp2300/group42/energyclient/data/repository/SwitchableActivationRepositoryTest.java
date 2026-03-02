package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchableActivationRepositoryTest {

    @Mock Activation dummyActivation;

    @Mock LocalActivationRepository localRepository;
    @Mock RemoteActivationRepository remoteRepository;
    @Mock SessionManager sessionManager;

    @InjectMocks SwitchableActivationRepository switchableRepository;

    @Test
    void add_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.add(dummyActivation)).thenReturn(dummyActivation);

        Activation result = switchableRepository.add(dummyActivation);

        assertEquals(dummyActivation, result);
        verify(remoteRepository).add(dummyActivation);
        verifyNoInteractions(localRepository);
    }

    @Test
    void add_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.add(dummyActivation)).thenReturn(dummyActivation);

        Activation result = switchableRepository.add(dummyActivation);

        assertEquals(dummyActivation, result);
        verify(localRepository).add(dummyActivation);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void get_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.get(100L, 1L)).thenReturn(dummyActivation);

        Activation result = switchableRepository.get(100L, 1L);

        assertEquals(dummyActivation, result);
        verify(remoteRepository).get(100L, 1L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void get_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.get(100L, 1L)).thenReturn(dummyActivation);

        Activation result = switchableRepository.get(100L, 1L);

        assertEquals(dummyActivation, result);
        verify(localRepository).get(100L, 1L);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void getAll_WhenLoggedIn_ShouldUseRemoteRepository() {
        List<Activation> expectedList = List.of(dummyActivation);
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getAll(100L)).thenReturn(expectedList);

        List<Activation> result = switchableRepository.getAll(100L);

        assertEquals(expectedList, result);
        verify(remoteRepository).getAll(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void getAll_WhenNotLoggedIn_ShouldUseLocalRepository() {
        List<Activation> expectedList = List.of(dummyActivation);
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getAll(100L)).thenReturn(expectedList);

        List<Activation> result = switchableRepository.getAll(100L);

        assertEquals(expectedList, result);
        verify(localRepository).getAll(100L);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void update_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.update(dummyActivation)).thenReturn(dummyActivation);

        Activation result = switchableRepository.update(dummyActivation);

        assertEquals(dummyActivation, result);
        verify(remoteRepository).update(dummyActivation);
        verifyNoInteractions(localRepository);
    }

    @Test
    void update_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.update(dummyActivation)).thenReturn(dummyActivation);

        Activation result = switchableRepository.update(dummyActivation);

        assertEquals(dummyActivation, result);
        verify(localRepository).update(dummyActivation);
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