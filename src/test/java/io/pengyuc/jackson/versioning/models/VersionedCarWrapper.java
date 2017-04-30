package io.pengyuc.jackson.versioning.models;

import io.pengyuc.jackson.versioning.annotations.JsonVersioned;

/**
 * A versioned class wrapping the versioned car as a property
 */
@JsonVersioned("1.0")
public class VersionedCarWrapper {
    private VersionedCar car;

    public VersionedCarWrapper(VersionedCar car) {
        this.car = car;
    }

    public VersionedCar getCar() {
        return car;
    }

    public void setCar(VersionedCar car) {
        this.car = car;
    }

}
