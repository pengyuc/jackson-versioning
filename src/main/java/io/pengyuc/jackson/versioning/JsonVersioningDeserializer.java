
/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Pengyu Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import io.pengyuc.jackson.versioning.annotations.JsonVersionProperty;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

import java.io.IOException;

/**
 *
 */
public class JsonVersioningDeserializer extends BeanDeserializer {
    private final  DeserializationConfig config;
    private final  BeanDescription beanDesc;
    private final  BeanDeserializer deserializer;

    private final Version modelVersion;
    private final BeanPropertyDefinition jsonVersionProperty;

    public JsonVersioningDeserializer(
            JsonVersioned jsonVersionedAnnotation,
            DeserializationConfig config,
            BeanDescription beanDesc,
            BeanDeserializer deserializer) {
        super(deserializer);
        this.config = config;
        this.beanDesc = beanDesc;
        this.deserializer = deserializer;

        modelVersion = Version.fromString(jsonVersionedAnnotation.value());
        BeanPropertyDefinition versionProperty = null;
        for (BeanPropertyDefinition propertyDef: beanDesc.findProperties()) {
            if ((propertyDef.hasGetter() && propertyDef.getGetter().hasAnnotation(JsonVersionProperty.class))
                    || (propertyDef.hasField() && propertyDef.getField().hasAnnotation(JsonVersionProperty.class))) {
                // Only support one attribute with JsonVersionProperty annotation
                versionProperty = propertyDef;
                break;
            }
        }
        jsonVersionProperty = versionProperty;

    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        final ObjectNode jsonNode = jsonParser.readValueAsTree();
        Version jsonVersion = modelVersion;
        try {
            if (jsonVersionProperty != null && jsonNode.has(jsonVersionProperty.getName())) {
                    jsonVersion = Version.fromString(jsonNode.get(jsonVersionProperty.getName()).asText());
            } else {
                Object jsonVersionObj = ctx.getAttribute(Version.JsonVersionConfigDeserializing);
                if (jsonVersionObj != null) {
                    if (jsonVersionObj instanceof Version)
                        jsonVersion = (Version) jsonVersionObj;
                    else
                        jsonVersion = Version.fromString(jsonVersionObj.toString());
                }
            }
        } catch (IllegalArgumentException e) {
            throw ctx.mappingException("Failed to parse version string: %s", e.getMessage(), e);
        }

        if (modelVersion.compareTo(jsonVersion) < 0) {
            throw ctx.mappingException(
                    "JSON version (%s) is greater than the latest model version (%s)",
                    jsonVersion.toString(), modelVersion.toString());
        }

        Optional<BeanPropertyDefinition> propertyNotInVersion = FluentIterable.from(beanDesc.findProperties())
                // Find the properties that should not be in this version of json
                .filter(Predicates.not(JsonVersioningPredicate.forPropertyDefInVersion(jsonVersion)))
                // see if any of those invalid property names is in the json
                .firstMatch(new Predicate<BeanPropertyDefinition>() {
                    @Override
                    public boolean apply(BeanPropertyDefinition beanPropertyDefinition) {
                        return jsonNode.has(beanPropertyDefinition.getName());
                    }
                });

        if (propertyNotInVersion.isPresent()) {
            throw ctx.mappingException("Property \"%s\" is not in version %s",
                    propertyNotInVersion.get().getName(), jsonVersion.toString());
        }

        // If there is no json version in the json body, insert the version to the json version property (if exists)
        if (jsonVersionProperty != null &&
                Strings.isNullOrEmpty(jsonNode.findPath(jsonVersionProperty.getName()).asText()))
            jsonNode.put(jsonVersionProperty.getName(), jsonVersion.toString());

        JsonParser postInterceptionParser = new TreeTraversingParser(jsonNode, jsonParser.getCodec());
        postInterceptionParser.nextToken();
        return deserializer.deserialize(postInterceptionParser, ctx);

    }
}
