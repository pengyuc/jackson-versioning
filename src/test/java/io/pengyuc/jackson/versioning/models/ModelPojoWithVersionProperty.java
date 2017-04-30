package io.pengyuc.jackson.versioning.models;

import io.pengyuc.jackson.versioning.annotations.JsonUntil;
import io.pengyuc.jackson.versioning.annotations.JsonVersionProperty;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

@JsonVersioned("1.0")
public class ModelPojoWithVersionProperty {
    @JsonVersionProperty
    private String version;

    @JsonUntil("0.8")
    private String deprecatedAt08 = "SomethingOld";

    @JsonUntil("0.9")
    private String deprecatedAt09 = "SomethingOld";

    private String alwaysThereAttribute = "AlwaysThere";

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeprecatedAt08() {
        return deprecatedAt08;
    }

    public String getDeprecatedAt09() {
        return deprecatedAt09;
    }

    public String getAlwaysThereAttribute() {
        return alwaysThereAttribute;
    }
}
