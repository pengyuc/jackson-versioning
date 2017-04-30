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
import io.pengyuc.jackson.versioning.models.CarWrapper;
import io.pengyuc.jackson.versioning.models.VersionedCar;
import io.pengyuc.jackson.versioning.models.VersionedCarWrapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

/**
 * Test versioned objects as properties of another model
 */
@RunWith(Parameterized.class)
public class TestVersionedAsProperty {
    private static ObjectMapper mapper;

    @BeforeClass
    static public void setupClass() {
        mapper = new ObjectMapper().registerModule(new JsonVersioningModule());
    }

    @Parameters
    public static Object[] params() {
        VersionedCar versionedCar = new VersionedCar(5, "rav4", "toy", true);
        return new Object[] {
                new CarWrapper(versionedCar),
                new VersionedCarWrapper(versionedCar)
        };
    }
    @Parameter
    public Object model;

    @Test
    public void versionedObjectAsProperty_StillRespectsVersioningAnnotations() throws JsonProcessingException {
        String s  =mapper.writer().withAttribute(Version.JsonVersionConfigSerializing, "1.0").writeValueAsString(model);

        assertThatJson(s).node("car.capacity").isAbsent();
        assertThatJson(s).node("car.model").isPresent();
    }

}
