package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

/**
 * Verifying that normal model POJO still gets serialized
 */
public class TestNormalModel {
    private static final int CAPACITY_VALUE = 5;
    private static final String MODEL_VALUE = "Rav4";
    private static final String MAKE_VALUE = "Toyota";
    private static final String CAPACITY_ATTR = "capacity";
    private static final String MODEL_ATTR = "model";
    private static final String MAKE_ATTR = "make";

    private static UnversionedCar unversionedCar;
    private static ObjectMapper mapper;

    public static class UnversionedCar {
        private Integer capacity;
        private String model;
        private String make;

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

    @BeforeClass
    static public void setupClass() {
        mapper = new ObjectMapper().registerModule(new JsonVersioningModule());
        unversionedCar = new UnversionedCar(CAPACITY_VALUE, MODEL_VALUE, MAKE_VALUE);
    }

    @Test
    public void normalModeSerializing_HasAllProperties() throws JsonProcessingException {
        String s = mapper.writer().writeValueAsString(unversionedCar);
        assertThatJson(s).node(CAPACITY_ATTR).isEqualTo(CAPACITY_VALUE);
        assertThatJson(s).node(MODEL_ATTR).isEqualTo(MODEL_VALUE);
        assertThatJson(s).node(MAKE_ATTR).isEqualTo(MAKE_VALUE);
    }

}
