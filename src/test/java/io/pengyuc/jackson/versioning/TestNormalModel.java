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
