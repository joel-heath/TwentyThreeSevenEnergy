package uk.ac.soton.comp2300.group42.extensions;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidatorExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private ValidatorFactory factory;
    private Validator validator;

    @Override
    public void beforeAll(@NonNull ExtensionContext context) {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public void afterAll(@NonNull ExtensionContext context) {
        if (factory != null) {
            factory.close();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(Validator.class);
    }

    @Override
    public Object resolveParameter(@NonNull ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
        return validator;
    }
}