package io.pengyuc.jackson.versioning.annotations;

import java.lang.annotation.*;

/**
 * A converter method that convert the model between versions. This annotation can be used with {@link JsonSince} and
 * {@link JsonUntil} to limit the invocation versions of the JSON.
 * The converter method shall manipulate the JsonNode object in this order:
 * Deserializing:
 * Input String -> JsonNode -> other versioning annotations; ex: {@link JsonSince} and/or {@link JsonUntil}
 *              -> converter -> versioned JsonNode -> Jackson deserialize into a model instance.
 * Serializing:
 * Model instance -> Jackson deserialize into JsonNode -> other versioning annotations
 *              -> converter -> Jackson serialize into string
 *
 * If there are multiple converter, the order of their execution is not guaranteed.
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonVersionConverter {
}
