package io.pengyuc.jackson.versioning.models;

import io.pengyuc.jackson.versioning.annotations.JsonUntil;
import io.pengyuc.jackson.versioning.annotations.JsonVersionProperty;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;
@JsonVersioned("1.0")
public class ModelPojoWithVersionProperty {
    @JsonVersionProperty
    private String version;

    @JsonUntil("0.8")
    private String deprecatedAt08;

    @JsonUntil("0.9")
    private String deprecatedAt09;

    private String alwaysThereAttribute;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeprecatedAt08() {
        return deprecatedAt08;
    }

    public void setDeprecatedAt08(String deprecatedAt08) {
        this.deprecatedAt08 = deprecatedAt08;
    }

    public String getDeprecatedAt09() {
        return deprecatedAt09;
    }

    public void setDeprecatedAt09(String deprecatedAt09) {
        this.deprecatedAt09 = deprecatedAt09;
    }

    public String getAlwaysThereAttribute() {
        return alwaysThereAttribute;
    }

    public void setAlwaysThereAttribute(String alwaysThereAttribute) {
        this.alwaysThereAttribute = alwaysThereAttribute;
    }
}
