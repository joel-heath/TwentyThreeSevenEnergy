package uk.ac.soton.comp2300.group42.extensions;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import tools.jackson.databind.json.JsonMapper;

public class JacksonTesterExtension implements ParameterResolver {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(JacksonTester.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
        ResolvableType type = ResolvableType.forMethodParameter(MethodParameter.forParameter(parameterContext.getParameter()));
        ResolvableType targetType = type.hasGenerics() ? type.getGeneric(0) : ResolvableType.forClass(Object.class);

        Class<?> testClass = extensionContext.getRequiredTestClass();

        return new JacksonTester<>(testClass, targetType, MAPPER);
    }
}