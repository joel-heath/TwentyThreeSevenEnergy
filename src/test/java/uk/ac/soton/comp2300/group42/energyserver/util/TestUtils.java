package uk.ac.soton.comp2300.group42.energyserver.util;

import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

public final class TestUtils {

    private TestUtils() {}

    public static <T> T assignId(InvocationOnMock invocation, Long id) {
        T entity = invocation.getArgument(0);
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    public static <T> T verifySaveAndCapture(CrudRepository<T, Long> repository, Class<T> clazz) {
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(clazz);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }
}