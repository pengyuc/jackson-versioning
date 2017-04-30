package io.pengyuc.jackson.versioning.annotations;


import io.pengyuc.jackson.versioning.Version;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate the property that has the version of the model.
 * The annotated field can be of string, or {@link Version} type.
 * This is used when the version should be included in the JSON content.
 * However, it is not required for the model class to be versioned.
 */
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonVersionProperty {
}
