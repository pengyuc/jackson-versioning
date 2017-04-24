package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.google.common.collect.Collections2;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Bean serializer that would check the targeting version and skip properties when needed.
 */
public class JsonVersioningSerializer extends BeanSerializer implements ResolvableSerializer {
    private final JsonVersioned jsonVersionedAnnotation;
    private final BeanDescription beanDesc;
    private final Version modelVersion;

    public JsonVersioningSerializer(JsonVersioned jsonVersionedAnnotation, BeanDescription beanDesc, BeanSerializerBase serializer) {
        super(serializer);
        this.jsonVersionedAnnotation = jsonVersionedAnnotation;
        this.beanDesc = beanDesc;
        this.modelVersion = Version.fromString(jsonVersionedAnnotation.value());
    }

    public JsonVersioningSerializer(JsonVersioningSerializer jvSer, Set<String> toIgnore) {
        super(jvSer, toIgnore);
        this.jsonVersionedAnnotation = jvSer.jsonVersionedAnnotation;
        this.beanDesc = jvSer.beanDesc;
        this.modelVersion = Version.fromString(jsonVersionedAnnotation.value());
    }

    public JsonVersioningSerializer(JsonVersioningSerializer jvSer, ObjectIdWriter objectIdWriter) {
        super(jvSer, objectIdWriter);
        this.jsonVersionedAnnotation = jvSer.jsonVersionedAnnotation;
        this.beanDesc = jvSer.beanDesc;
        this.modelVersion = Version.fromString(jsonVersionedAnnotation.value());
    }

    public JsonVersioningSerializer(JsonVersioningSerializer jvSer, ObjectIdWriter objectIdWriter, Object filterId) {
        super(jvSer, objectIdWriter, filterId);
        this.jsonVersionedAnnotation = jvSer.jsonVersionedAnnotation;
        this.beanDesc = jvSer.beanDesc;
        this.modelVersion = Version.fromString(jsonVersionedAnnotation.value());
    }

    @Override
    public JsonVersioningSerializer withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new JsonVersioningSerializer(this, objectIdWriter);
    }

    @Override
    protected JsonVersioningSerializer withIgnorals(Set<String> toIgnore) {
        return new JsonVersioningSerializer(this, toIgnore);
    }

    @Override
    public JsonVersioningSerializer withFilterId(Object filterId) {
        return new JsonVersioningSerializer(this, _objectIdWriter, filterId);
    }

    @Override
    protected BeanSerializerBase asArraySerializer() {
        return new BeanAsArraySerializer(this);
    }


    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        super.resolve(provider);
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
        Object jsonVersionObj = provider.getAttribute(Version.JsonVersionProperty);
        if (jsonVersionObj == null) {
            jsonVersion = modelVersion;
        } else if (jsonVersionObj instanceof Version) {
            jsonVersion = (Version) jsonVersionObj;
        } else {
            jsonVersion = Version.fromString(jsonVersionObj.toString());
        }

        if (modelVersion.compareTo(jsonVersion) < 0) {
            throw new JsonGenerationException("JSON version (" + jsonVersion.toString() + ")" +
                    " is greater than the latest model version (" + modelVersion.toString() + ")", gen);
        }

        final Collection<BeanPropertyWriter> props;
        if (_filteredProps != null && provider.getActiveView() != null) {
            props = Collections2.filter(Arrays.asList(_filteredProps), new JsonVersioningPredicate(jsonVersion));
        } else {
            props = Collections2.filter(Arrays.asList(_props), new JsonVersioningPredicate(jsonVersion));
        }

        final PropertyFilter filter = _propertyFilterId == null ? null : findPropertyFilter(provider, _propertyFilterId, bean);
        for (BeanPropertyWriter prop : props) {
            if (prop != null) { // can have nulls in filtered list
                try {
                    if (filter == null)
                        prop.serializeAsField(bean, gen, provider);
                    else
                        filter.serializeAsField(bean, gen, provider, prop);
                } catch (Exception e) {
                    wrapAndThrow(provider, e, bean, prop.getName());
                }
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
