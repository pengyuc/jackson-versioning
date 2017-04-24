package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

/**
 * Registering this Json Versioning module to the Jackson
 */
public class JsonVersioningModule extends SimpleModule {
    public JsonVersioningModule() {
        setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                JsonVersioned jsonVersioned = beanDesc.getClassAnnotations().get(JsonVersioned.class);
                if (jsonVersioned != null) {
                    return new JsonVersioningDeserializer(jsonVersioned, config, beanDesc, deserializer);
                }
                return super.modifyDeserializer(config, beanDesc, deserializer);
            }
        });

        setSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                JsonVersioned jsonVersioned = beanDesc.getClassAnnotations().get(JsonVersioned.class);
                if (jsonVersioned != null && BeanSerializerBase.class.isInstance(serializer)) {
                    return new JsonVersioningSerializer(jsonVersioned, beanDesc, (BeanSerializerBase) serializer);
                }
                return super.modifySerializer(config, beanDesc, serializer);
            }
        });
    }
}
