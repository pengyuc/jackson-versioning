package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.google.common.collect.Collections2;
import io.pengyuc.jackson.versioning.annotations.JsonVersionProperty;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Bean serializer that would check the targeting version and skip properties when needed.
 */
public class JsonVersioningSerializer extends BeanSerializer implements ResolvableSerializer {
    private final JsonVersioned jsonVersionedAnnotation;
    private final BeanDescription beanDesc;
    private final Version modelVersion;
    private final BeanPropertyDefinition jsonVersionProperty;

    public JsonVersioningSerializer(JsonVersioned jsonVersionedAnnotation, BeanDescription beanDesc, BeanSerializerBase serializer) {
        super(serializer);
        this.jsonVersionedAnnotation = jsonVersionedAnnotation;
        this.beanDesc = beanDesc;
        this.modelVersion = Version.fromString(jsonVersionedAnnotation.value());

        BeanPropertyDefinition versionProperty = null;
        for (BeanPropertyDefinition propertyDef: beanDesc.findProperties()) {
            if (propertyDef.getAccessor().hasAnnotation(JsonVersionProperty.class)) {
                // Only support one attribute with JsonVersionProperty annotation
                versionProperty = propertyDef;
                break;
            }
        }
        jsonVersionProperty = versionProperty;
    }

    @Override
    protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        serializeFieldsWithVersioning(bean, gen, provider);
    }

    @Override
    protected void serializeFieldsFiltered(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        serializeFieldsWithVersioning(bean, gen, provider);
    }

    protected void serializeFieldsWithVersioning(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Version jsonVersion;
        Object jsonVersionObj = null;
        boolean forceOutputVersion = false;
        if (jsonVersionProperty != null) {
            jsonVersionObj = jsonVersionProperty.getAccessor().getValue(bean);
        }
        if (jsonVersionObj == null) {
            jsonVersionObj = provider.getAttribute(Version.JsonVersionConfig);
            // There is a version property and it was not set. We should output the version at this property
            if (jsonVersionProperty != null)
                forceOutputVersion = true;
        }

        if (jsonVersionObj == null) {
            jsonVersion = modelVersion;
            // There is a version property and it was not set. We should output the version at this property
            if (jsonVersionProperty != null)
                forceOutputVersion = true;
        } else if (jsonVersionObj instanceof Version) {
            jsonVersion = (Version) jsonVersionObj;
        } else {
            jsonVersion = Version.fromString(jsonVersionObj.toString());
        }

        if (modelVersion.compareTo(jsonVersion) < 0) {
            throw new JsonGenerationException("JSON version (" + jsonVersion.toString() + ")" +
                    " is greater than the latest model version (" + modelVersion.toString() + ")", gen);
        }

        final Collection<BeanPropertyWriter> properties;
        if (_filteredProps != null && provider.getActiveView() != null) {
            properties = Collections2.filter(Arrays.asList(_filteredProps), new JsonVersioningPredicate(jsonVersion));
        } else {
            properties = Collections2.filter(Arrays.asList(_props), new JsonVersioningPredicate(jsonVersion));
        }

        final PropertyFilter filter = _propertyFilterId == null ? null : findPropertyFilter(provider, _propertyFilterId, bean);
        for (BeanPropertyWriter property : properties) {
            if (property == null)
                continue;

            if (forceOutputVersion && property.getAnnotation(JsonVersionProperty.class) != null) {
                gen.writeFieldName(property.getSerializedName());
                gen.writeString(jsonVersion.toString());
                continue;
            }
            try {
                if (filter == null)
                    property.serializeAsField(bean, gen, provider);
                else
                    filter.serializeAsField(bean, gen, provider, property);
            } catch (Exception e) {
                wrapAndThrow(provider, e, bean, property.getName());
            }
        }
        if (_anyGetterWriter != null) {
            try {
                _anyGetterWriter.getAndFilter(bean, gen, provider, filter);
            } catch (Exception e) {
                wrapAndThrow(provider, e, bean, "[anySetter]");
            }
        }
    }

}
