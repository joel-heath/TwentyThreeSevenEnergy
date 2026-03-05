package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ModelUtilsTest {

    @Mock
    private Consumer<String> mockSetter;

    @Test
    void updateIfChangedValuesEqualDoesNotCallSetter() {
        ModelUtils.updateIfChanged("Same", "Same", mockSetter);

        then(mockSetter).shouldHaveNoInteractions();
    }

    @Test
    void updateIfChangedValuesNotEqualCallsSetter() {
        ModelUtils.updateIfChanged("Old", "New", mockSetter);

        then(mockSetter).should().accept("New");
    }

    @Test
    void updateIfChangedValueToNullCallsSetter() {
        ModelUtils.updateIfChanged("Old", null, mockSetter);

        then(mockSetter).should().accept(null);
    }

    @Test
    void updateIfChangedNullToValueCallsSetter() {
        ModelUtils.updateIfChanged(null, "New", mockSetter);

        then(mockSetter).should().accept("New");
    }

    @Test
    void updateIfChangedBothNullDoesNotCallSetter() {
        ModelUtils.updateIfChanged((String) null, null, mockSetter);

        then(mockSetter).shouldHaveNoInteractions();
    }

    @Test
    void updateIfChangedWithIdIdsEqualDoesNotCallSetter() {
        ModelUtils.updateIfChanged(1L, 1L, "New Value", mockSetter);

        then(mockSetter).shouldHaveNoInteractions();
    }

    @Test
    void updateIfChangedWithIdIdsNotEqualCallsSetter() {
        ModelUtils.updateIfChanged(1L, 2L, "New Value", mockSetter);

        then(mockSetter).should().accept("New Value");
    }

    @Test
    void updateIfChangedWithIdIdToNullCallsSetter() {
        ModelUtils.updateIfChanged(1L, null, "New Value", mockSetter);

        then(mockSetter).should().accept("New Value");
    }

    @Test
    void updateIfChangedWithIdNullToIdCallsSetter() {
        ModelUtils.updateIfChanged(null, 2L, "New Value", mockSetter);

        then(mockSetter).should().accept("New Value");
    }

    @Test
    void updateIfChangedWithIdBothIdsNullDoesNotCallSetter() {
        ModelUtils.updateIfChanged((Long) null, null, "New Value", mockSetter);

        then(mockSetter).shouldHaveNoInteractions();
    }
}