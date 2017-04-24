package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.google.common.base.Predicate;
import io.pengyuc.jackson.versioning.annotations.JsonSince;
import io.pengyuc.jackson.versioning.annotations.JsonUntil;

/**
 * Conditions to filter out properties that are not suitable for the json version
 */
public class JsonVersioningPredicate implements Predicate<PropertyWriter> {
    private Version jsonVersion;

    public JsonVersioningPredicate(Version jsonVersion) {
        this.jsonVersion = jsonVersion;
    }

    public boolean apply(PropertyWriter prop) {
        JsonSince since = prop.getAnnotation(JsonSince.class);
        JsonUntil until = prop.getAnnotation(JsonUntil.class);
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
