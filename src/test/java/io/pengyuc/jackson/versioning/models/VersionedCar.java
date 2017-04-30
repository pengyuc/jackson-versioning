package io.pengyuc.jackson.versioning.models;

import io.pengyuc.jackson.versioning.annotations.JsonSince;
import io.pengyuc.jackson.versioning.annotations.JsonUntil;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

@JsonVersioned("1.0")
public class VersionedCar {
    private Integer capacity = 5;
    private String model = "Rav4";
    private String make = "Toyota";

    // annotations should work whether it is put on the fields or on the getters
    @JsonSince("0.2")
    @JsonUntil("0.7")
    private Boolean validBetween2to7 = true;

    @JsonUntil("0.2")
    @JsonSince("0.7")
    private Boolean omittedBetween2to6 = true;

    public VersionedCar() {
    }

    public VersionedCar(Integer capacity, String model, String make, Boolean validBetween2to7) {
        this.capacity = capacity;
        this.model = model;
        this.make = make;
        this.validBetween2to7 = validBetween2to7;
    }

    @JsonUntil("0.9")
    public Integer getCapacity() {
        return capacity;
    }

    @JsonSince("0.8")
    public String getModel() {
        return model;
    }

    public String getMake() {
        return make;
    }

    public Boolean getValidBetween2to7() {
        return validBetween2to7;
    }

    public Boolean getOmittedBetween2to6() {
        return omittedBetween2to6;
    }
}
