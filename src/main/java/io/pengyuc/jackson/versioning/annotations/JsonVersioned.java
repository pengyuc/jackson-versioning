package io.pengyuc.jackson.versioning.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that the annotated model needs versioning support.
 * The value shall be the latest version number in String.
 */
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonVersioned {
    /** version number in string can be \d+(\.\d+)* ex: "1", "1.0.0", "1.0.1", etc.  */
    String value();
}
