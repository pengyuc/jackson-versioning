package io.pengyuc.jackson.versioning.models;

/**
 * A class wrapping the versioned car as a property
 */
public class CarWrapper {
    private VersionedCar car;

    public CarWrapper(VersionedCar car) {
        this.car = car;
    }

    public VersionedCar getCar() {
        return car;
    }

    public void setCar(VersionedCar car) {
        this.car = car;
    }
}
