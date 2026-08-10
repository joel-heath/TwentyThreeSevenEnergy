package uk.ac.soton.comp2300.group42.extensions;

import org.junit.jupiter.api.extension.ExtendWith;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({JacksonTesterExtension.class, ValidatorExtension.class})
public @interface ApiContractTest {
}