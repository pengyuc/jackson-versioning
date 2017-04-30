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

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Predicate;
import io.pengyuc.jackson.versioning.annotations.JsonSince;
import io.pengyuc.jackson.versioning.annotations.JsonUntil;

import java.lang.annotation.Annotation;

/**
 * Conditions to filter out properties that are not suitable for the json version
 */
public class JsonVersioningPredicate {
    static public <T extends BeanProperty> Predicate<T> forPropertyInVersion(final Version jsonVersion) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T property) {
                JsonSince since = property.getAnnotation(JsonSince.class);
                JsonUntil until = property.getAnnotation(JsonUntil.class);
                return isWithinVersion(since, until, jsonVersion);
            }
        };
    }

    static public <T extends BeanPropertyDefinition> Predicate<T> forPropertyDefInVersion(final Version jsonVersion) {
        return new Predicate<T>() {
            private <A extends Annotation> A getAnnotation(T property, Class<A> acls) {
                A annoClz = null;
                if (property.hasGetter()) {
                    annoClz = property.getGetter().getAnnotation(acls);
                }
                if (property.hasField() && annoClz == null) {
                    annoClz = property.getField().getAnnotation(acls);
                }
                return annoClz;
            }

            @Override
            public boolean apply(T property) {
                JsonSince since = getAnnotation(property, JsonSince.class);
                JsonUntil until = getAnnotation(property, JsonUntil.class);
                return isWithinVersion(since, until, jsonVersion);
            }
        };
    }

    static private boolean isWithinVersion(JsonSince since, JsonUntil until, Version jsonVersion) {
        if (since == null && until == null)
            return true;

        Version sinceVersion = since == null ? null : Version.fromString(since.value());
        Version untilVersion = until == null ? null : Version.fromString(until.value());

        if (sinceVersion != null && untilVersion != null && sinceVersion.compareTo(untilVersion) <= 0) {
            // since and until form a valid zone
            return jsonVersion.compareTo(sinceVersion) >= 0 && jsonVersion.compareTo(untilVersion) < 0;
        }
        if (untilVersion != null && jsonVersion.compareTo(untilVersion) < 0)
            return true;

        if (sinceVersion != null && jsonVersion.compareTo(sinceVersion) >= 0)
            return true;

        return false;
    }
}
