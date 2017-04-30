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
import com.fasterxml.jackson.databind.ObjectWriter;
import io.pengyuc.jackson.versioning.models.ModelPojoWithVersionProperty;
import org.junit.Before;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class TestSerializingWithJsonVersionProperty {
    private static final ObjectWriter writer = new ObjectMapper().registerModule(new JsonVersioningModule()).writer();
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_DEPRECATED_AT_08 = "deprecatedAt08";
    private static final String ATTR_DEPRECATED_AT_09 = "deprecatedAt09";
    private static final String ATTR_ALWAYS_THERE_ATTRIBUTE = "alwaysThereAttribute";
    private ModelPojoWithVersionProperty pojo;

    @Before
    public void setup() {
        pojo = new ModelPojoWithVersionProperty();
        pojo.setDeprecatedAt08("DepAt08");
        pojo.setDeprecatedAt09("DepAt09");
        pojo.setAlwaysThereAttribute("alwaysThere");
    }

    @Test
    public void setTargetVersionInProperty_ShouldOverrideConfiguredVersion() throws JsonProcessingException {
        pojo.setVersion("0.8");
        String s = writer
                .withAttribute(Version.JsonVersionConfigSerializing, "0.9")
                .writeValueAsString(pojo);

        assertThatJson(s).node(ATTR_VERSION).isStringEqualTo("0.8");
        assertThatJson(s).node(ATTR_DEPRECATED_AT_08).isAbsent();
        assertThatJson(s).node(ATTR_DEPRECATED_AT_09).isPresent();
        assertThatJson(s).node(ATTR_ALWAYS_THERE_ATTRIBUTE).isPresent();
    }

    @Test
    public void noTargetVersionInProperty_ShouldUseConfiguredVersion_ShouldShowVersionInProperty() throws JsonProcessingException {
        String s = writer
                .withAttribute(Version.JsonVersionConfigSerializing, "0.9")
                .writeValueAsString(pojo);

        assertThatJson(s).node(ATTR_VERSION).isStringEqualTo("0.9");
        assertThatJson(s).node(ATTR_DEPRECATED_AT_08).isAbsent();
        assertThatJson(s).node(ATTR_DEPRECATED_AT_09).isAbsent();
        assertThatJson(s).node(ATTR_ALWAYS_THERE_ATTRIBUTE).isPresent();
    }

    @Test
    public void noTargetVersionInPropertyOrConfig_ShouldUseModelVersion_ShouldShowVersionInProperty() throws JsonProcessingException {
        String s = writer.writeValueAsString(pojo);

        assertThatJson(s).node(ATTR_VERSION).isStringEqualTo("1.0");
        assertThatJson(s).node(ATTR_DEPRECATED_AT_08).isAbsent();
        assertThatJson(s).node(ATTR_DEPRECATED_AT_09).isAbsent();
        assertThatJson(s).node(ATTR_ALWAYS_THERE_ATTRIBUTE).isPresent();
    }
}
