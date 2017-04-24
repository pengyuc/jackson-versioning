package io.pengyuc.jackson.versioning.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If the the serializing version number is greater than or equal to the version number given in this annotation,
 * The annotated attribute won't be serialized. This annotation can be put on the getter/setter of the attribute.
 * If the deserializing version number is greater than or equal to the version number given in this annotation,
 * and the annotated attribute exists, the deserialization will fail.
 *
 * This annotation can be used with {@link JsonVersionConverter}.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonUntil {
    /** version number at which the attribute is no longer used */
    String value();
}
