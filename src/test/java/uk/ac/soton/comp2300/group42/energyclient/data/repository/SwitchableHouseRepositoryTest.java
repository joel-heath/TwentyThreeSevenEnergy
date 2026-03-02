package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwitchableHouseRepositoryTest {

    @Mock House dummyHouse;
    @Mock Housemate dummyHousemate;
    @Mock List<Housemate> dummyHousemates;

    @Mock LocalHouseRepository localRepository;
    @Mock RemoteHouseRepository remoteRepository;
    @Mock SessionManager sessionManager;

    @InjectMocks SwitchableHouseRepository switchableRepository;

    @Test
    void add_NoArgs_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);

        switchableRepository.add();

        verify(remoteRepository).add();
        verifyNoInteractions(localRepository);
    }

    @Test
    void add_NoArgs_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);

        switchableRepository.add();

        verify(localRepository).add();
        verifyNoInteractions(remoteRepository);
    }

    @Test
    void add_WithHouse_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.add(dummyHouse)).thenReturn(dummyHouse);

        House result = switchableRepository.add(dummyHouse);

        assertEquals(dummyHouse, result);
        verify(remoteRepository).add(dummyHouse);
        verifyNoInteractions(localRepository);
    }

    @Test
    void add_WithHouse_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.add(dummyHouse)).thenReturn(dummyHouse);

        House result = switchableRepository.add(dummyHouse);

        assertEquals(dummyHouse, result);
        verify(localRepository).add(dummyHouse);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void get_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.get(100L)).thenReturn(dummyHouse);

        House result = switchableRepository.get(100L);

        assertEquals(dummyHouse, result);
        verify(remoteRepository).get(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void get_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.get(100L)).thenReturn(dummyHouse);

        House result = switchableRepository.get(100L);

        assertEquals(dummyHouse, result);
        verify(localRepository).get(100L);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void getAll_WhenLoggedIn_ShouldUseRemoteRepository() {
        List<House> expectedList = List.of(dummyHouse);
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getAll()).thenReturn(expectedList);

        List<House> result = switchableRepository.getAll();

        assertEquals(expectedList, result);
        verify(remoteRepository).getAll();
        verifyNoInteractions(localRepository);
    }

    @Test
    void getAll_WhenNotLoggedIn_ShouldUseLocalRepository() {
        List<House> expectedList = List.of(dummyHouse);
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getAll()).thenReturn(expectedList);

        List<House> result = switchableRepository.getAll();

        assertEquals(expectedList, result);
        verify(localRepository).getAll();
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void update_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.update(dummyHouse)).thenReturn(dummyHouse);

        House result = switchableRepository.update(dummyHouse);

        assertEquals(dummyHouse, result);
        verify(remoteRepository).update(dummyHouse);
        verifyNoInteractions(localRepository);
    }

    @Test
    void update_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.update(dummyHouse)).thenReturn(dummyHouse);

        House result = switchableRepository.update(dummyHouse);

        assertEquals(dummyHouse, result);
        verify(localRepository).update(dummyHouse);
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


    @Test
    void getCurrentUserAsHousemate_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getCurrentUserAsHousemate(100L)).thenReturn(dummyHousemate);

        Housemate result = switchableRepository.getCurrentUserAsHousemate(100L);

        assertEquals(dummyHousemate, result);
        verify(remoteRepository).getCurrentUserAsHousemate(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void getCurrentUserAsHousemate_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getCurrentUserAsHousemate(100L)).thenReturn(dummyHousemate);

        Housemate result = switchableRepository.getCurrentUserAsHousemate(100L);

        assertEquals(dummyHousemate, result);
        verify(localRepository).getCurrentUserAsHousemate(100L);
        verifyNoInteractions(remoteRepository);
    }


    @Test
    void getHousemates_WhenLoggedIn_ShouldUseRemoteRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        when(remoteRepository.getHousemates(100L)).thenReturn(dummyHousemates);

        List<Housemate> result = switchableRepository.getHousemates(100L);

        assertEquals(dummyHousemates, result);
        verify(remoteRepository).getHousemates(100L);
        verifyNoInteractions(localRepository);
    }

    @Test
    void getHousemates_WhenNotLoggedIn_ShouldUseLocalRepository() {
        when(sessionManager.isLoggedIn()).thenReturn(false);
        when(localRepository.getHousemates(100L)).thenReturn(dummyHousemates);

        List<Housemate> result = switchableRepository.getHousemates(100L);

        assertEquals(dummyHousemates, result);
        verify(localRepository).getHousemates(100L);
        verifyNoInteractions(remoteRepository);
    }
}