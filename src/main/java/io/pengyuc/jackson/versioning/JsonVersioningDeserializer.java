package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

import java.io.IOException;

/**
 *
 */
public class JsonVersioningDeserializer<T> extends StdDeserializer<T> implements ResolvableDeserializer {
    private final  JsonVersioned jsonVersionedAnnotation;
    private final  DeserializationConfig config;
    private final  BeanDescription beanDesc;
    private final  JsonDeserializer<T> deserializer;

    private final Version modelVersion;

    public JsonVersioningDeserializer(
            JsonVersioned jsonVersionedAnnotation,
            DeserializationConfig config,
            BeanDescription beanDesc,
            JsonDeserializer<T> deserializer) {
        super(beanDesc.getType());
        this.jsonVersionedAnnotation = jsonVersionedAnnotation;
        this.config = config;
        this.beanDesc = beanDesc;
        this.deserializer = deserializer;

        modelVersion = Version.fromString(jsonVersionedAnnotation.value());
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
        return deserializer.deserialize(jsonParser, ctx);
    }

    public void resolve(DeserializationContext ctx) throws JsonMappingException {
        if (deserializer instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer) deserializer).resolve(ctx);
        }
    }
}
