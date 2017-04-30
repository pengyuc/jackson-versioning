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
