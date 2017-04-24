package io.pengyuc.jackson.versioning.models;

public class UnversionedCar {
    private Integer capacity = 5;
    private String model = "Rav4";
    private String make = "Toyota";

    public UnversionedCar(Integer capacity, String model, String make) {
        this.capacity = capacity;
        this.model = model;
        this.make = make;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getModel() {
        return model;
    }

    public String getMake() {
        return make;
    }
}
