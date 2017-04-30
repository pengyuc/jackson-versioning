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
