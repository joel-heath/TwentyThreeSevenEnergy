package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchableUserRepositoryTest {

    @Mock User dummyUser;
    @Mock Preferences dummyPreferences;

    @Mock LocalUserRepository localRepository;
    @Mock RemoteUserRepository remoteRepository;
    @Mock SessionManager sessionManager;

    @InjectMocks SwitchableUserRepository switchableRepository;

    @Test
    void getCurrent_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getCurrent()).thenReturn(dummyUser);

        User result = switchableRepository.getCurrent();

        assertEquals(dummyUser, result);
        verify(remoteRepository).getCurrent();
        verifyNoInteractions(localRepository);
    }

    @Test
    void getCurrent_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getCurrent()).thenReturn(dummyUser);

        User result = switchableRepository.getCurrent();

        assertEquals(dummyUser, result);
        verify(localRepository).getCurrent();
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void getCurrentPreferences_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getCurrentPreferences()).thenReturn(dummyPreferences);

        Preferences result = switchableRepository.getCurrentPreferences();

        assertEquals(dummyPreferences, result);
        verify(remoteRepository).getCurrentPreferences();
        verifyNoInteractions(localRepository);
    }

    @Test
    void getCurrentPreferences_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getCurrentPreferences()).thenReturn(dummyPreferences);

        Preferences result = switchableRepository.getCurrentPreferences();

        assertEquals(dummyPreferences, result);
        verify(localRepository).getCurrentPreferences();
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void get_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(remoteRepository.get(100L)).thenReturn(dummyUser);

        User result = switchableRepository.get(100L);

        assertEquals(dummyUser, result);
        verify(remoteRepository).get(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void get_WhenNotLoggedIn_ShouldUseRemoteRepository() {
        when(remoteRepository.get(100L)).thenReturn(dummyUser);

        User result = switchableRepository.get(100L);

        assertEquals(dummyUser, result);
        verify(remoteRepository).get(100L);
        verifyNoInteractions(localRepository);
    }


    @Test
    void update_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.update(dummyUser)).thenReturn(dummyUser);

        User result = switchableRepository.update(dummyUser);

        assertEquals(dummyUser, result);
        verify(remoteRepository).update(dummyUser);
        verifyNoInteractions(localRepository);
    }

    @Test
    void update_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.update(dummyUser)).thenReturn(dummyUser);

        User result = switchableRepository.update(dummyUser);

        assertEquals(dummyUser, result);
        verify(localRepository).update(dummyUser);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void delete_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);

        switchableRepository.delete(100L);

        verify(remoteRepository).delete(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void delete_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);

        switchableRepository.delete(100L);

        verify(localRepository).delete(100L);
        verifyNoInteractions(remoteRepository);
    }
}