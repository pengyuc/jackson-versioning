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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import io.pengyuc.jackson.versioning.models.VersionedCar;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class TestJsonSerializationVersioningWithSimpleAnnotations {
    private static final int CAPACITY_VALUE = 5;
    private static final String MODEL_VALUE = "Rav4";
    private static final String MAKE_VALUE = "Toyota";
    private static final boolean INSURED_VALUE = true;
    private static final String CAPACITY_ATTR = "capacity";
    private static final String MODEL_ATTR = "model";
    private static final String MAKE_ATTR = "make";
    private static final String VALID_BETWEEN_ATTR = "validBetween2to7";
    private static final String OMIITED_INBETWEEN_ATTR = "omittedBetween2to6";


    private static ObjectMapper mapper;
    private static VersionedCar versionedCar;

    @BeforeClass
    static public void setupClass() {
        mapper = new ObjectMapper().registerModule(new JsonVersioningModule());
        versionedCar = new VersionedCar(CAPACITY_VALUE, MODEL_VALUE, MAKE_VALUE, INSURED_VALUE);
    }

    private ContextAttributes makeVersionAttr(String versionStr) {
        return ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfigSerializing, versionStr);
    }

    @Test(expected = JsonGenerationException.class)
    public void serializeToAHigherVersion_ShallThrowException() throws JsonProcessingException {
        mapper.writer()
                .withAttribute(Version.JsonVersionConfigSerializing, "1.1")
                .writeValueAsString(versionedCar);
    }

    @Test
    public void serializeDeprecatedAttribute_ShallBeSkipped() throws JsonProcessingException {
        String s = mapper.writer()
                .withAttribute(Version.JsonVersionConfigSerializing, "0.9")
                .writeValueAsString(versionedCar);

        assertThatJson(s).node(CAPACITY_ATTR).isAbsent();
        assertThatJson(s).node(MODEL_ATTR).isEqualTo(MODEL_VALUE);
        assertThatJson(s).node(MAKE_ATTR).isEqualTo(MAKE_VALUE);
    }

    @Test
    public void serializeNewerAttribute_ShallBeSkipped() throws JsonProcessingException {
        String s = mapper.writer()
                .withAttribute(Version.JsonVersionConfigSerializing, "0.7")
                .writeValueAsString(versionedCar);

        assertThatJson(s).node(CAPACITY_ATTR).isEqualTo(CAPACITY_VALUE);
        assertThatJson(s).node(MODEL_ATTR).isAbsent();
        assertThatJson(s).node(MAKE_ATTR).isEqualTo(MAKE_VALUE);
    }

    @Test
    public void untilAfterSince_PropertyShallBeIncludedInBetween() throws JsonProcessingException {
        ObjectWriter writer = mapper.writer().withAttribute(Version.JsonVersionConfigSerializing, "0.1");
        String s = writer.writeValueAsString(versionedCar);
        assertThatJson(s).node(VALID_BETWEEN_ATTR).isAbsent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.2").writeValueAsString(versionedCar);
        assertThatJson(s).node(VALID_BETWEEN_ATTR).isPresent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.5").writeValueAsString(versionedCar);
        assertThatJson(s).node(VALID_BETWEEN_ATTR).isPresent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.7").writeValueAsString(versionedCar);
        assertThatJson(s).node(VALID_BETWEEN_ATTR).isAbsent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.9").writeValueAsString(versionedCar);
        assertThatJson(s).node(VALID_BETWEEN_ATTR).isAbsent();

    }

    @Test
    public void untilFirstSinceAfter_PropertyShallBeExcludedInBetween() throws JsonProcessingException {
        ObjectWriter writer = mapper.writer().withAttribute(Version.JsonVersionConfigSerializing, "0.1");
        String s = writer.writeValueAsString(versionedCar);
        assertThatJson(s).node(OMIITED_INBETWEEN_ATTR).isPresent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.2").writeValueAsString(versionedCar);
        assertThatJson(s).node(OMIITED_INBETWEEN_ATTR).isAbsent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.5").writeValueAsString(versionedCar);
        assertThatJson(s).node(OMIITED_INBETWEEN_ATTR).isAbsent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.7").writeValueAsString(versionedCar);
        assertThatJson(s).node(OMIITED_INBETWEEN_ATTR).isPresent();

        s = writer.withAttribute(Version.JsonVersionConfigSerializing, "0.9").writeValueAsString(versionedCar);
        assertThatJson(s).node(OMIITED_INBETWEEN_ATTR).isPresent();

    }


}
