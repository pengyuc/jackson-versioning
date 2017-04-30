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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import io.pengyuc.jackson.versioning.models.ModelPojoWithVersionProperty;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestDeserializingWithJsonVersionProperty {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JsonVersioningModule());;

    private static String JSON_VER_10 =
            "{\n" +
            "  \"version\": \"1.0\",\n" +
            "  \"alwaysThereAttribute\": \"alwaysThere\"\n" +
            "}";

    private static String JSON_VER_NOT_SPECIFIED =
            "{\n" +
                    "  \"alwaysThereAttribute\": \"alwaysThere\"\n" +
                    "}";

    private static String JSON_VER_06 =
            "{\n" +
            "  \"version\": \"0.6\",\n" +
            "  \"deprecatedAt08\": \"something08\",\n" +
            "  \"deprecatedAt09\": \"something09\",\n" +
            "  \"alwaysThereAttribute\": \"alwaysThere\"\n" +
            "}";

    private static String JSON_VER_08 =
            "{\n" +
            "  \"version\": \"0.8\",\n" +
            "  \"deprecatedAt09\": \"something09\",\n" +
            "  \"alwaysThereAttribute\": \"alwaysThere\"\n" +
            "}";

    private static String JSON_VER_09_WITH_OLD_ATTR =
            "{\n" +
            "  \"version\": \"0.9\",\n" +
            "  \"deprecatedAt08\": \"something08\",\n" +
            "  \"deprecatedAt09\": \"something09\",\n" +
            "  \"alwaysThereAttribute\": \"alwaysThere\"\n" +
            "}";


    private static String JSON_VER_NOT_SPECIFIED_WITH_OLD_ATTR =
            "{\n" +
            "  \"deprecatedAt08\": \"something08\",\n" +
            "  \"deprecatedAt09\": \"something09\",\n" +
            "  \"alwaysThereAttribute\": \"alwaysThere\"\n" +
            "}";

    @Test
    public void whenJsonVersionPropertyIsSet_OverrideConfigVersion() throws IOException {
        ModelPojoWithVersionProperty pojo = mapper.reader()
                .forType(ModelPojoWithVersionProperty.class)
                .with(ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfigDeserializing, "0.9"))
                .readValue(JSON_VER_06);

        Assert.assertEquals("0.6", pojo.getVersion());
        Assert.assertEquals("something08", pojo.getDeprecatedAt08());
        Assert.assertEquals("something09", pojo.getDeprecatedAt09());
        Assert.assertEquals("alwaysThere", pojo.getAlwaysThereAttribute());
    }

    @Test
    public void whenJsonVersionPropertyIsNotSet_UseConfiguredVersion() throws IOException {
        ModelPojoWithVersionProperty pojo = mapper.reader()
                .withAttribute(Version.JsonVersionConfigDeserializing, "0.6")
                .forType(ModelPojoWithVersionProperty.class)
                .readValue(JSON_VER_NOT_SPECIFIED_WITH_OLD_ATTR);

        Assert.assertEquals("0.6", pojo.getVersion());
        Assert.assertEquals("something08", pojo.getDeprecatedAt08());
        Assert.assertEquals("something09", pojo.getDeprecatedAt09());
        Assert.assertEquals("alwaysThere", pojo.getAlwaysThereAttribute());
    }

    @Test
    public void whenJsonVersionPropertyIsNotSetAndNoVersionConfigured_UseModelVersion() throws IOException {
        ModelPojoWithVersionProperty pojo = mapper.reader()
                .forType(ModelPojoWithVersionProperty.class)
                .readValue(JSON_VER_NOT_SPECIFIED);

        Assert.assertEquals("1.0", pojo.getVersion());
        Assert.assertNull(pojo.getDeprecatedAt08());
        Assert.assertNull(pojo.getDeprecatedAt09());
        Assert.assertEquals("alwaysThere", pojo.getAlwaysThereAttribute());
    }

    @Test (expected = JsonMappingException.class)
    public void whenJsonVersionPropertyIsNotSetAndNoVersionConfigured_UseModelVersion_AndFailDeprecatedAttributes() throws IOException {
        mapper.reader()
                .forType(ModelPojoWithVersionProperty.class)
                .readValue(JSON_VER_NOT_SPECIFIED_WITH_OLD_ATTR);
    }

    @Test (expected = JsonMappingException.class)
    public void whenJsonVersionPropertyIsNotSetAndConfiguredVersionUsed_FailDeprecatedAttributes() throws IOException {
        mapper.reader()
                .withAttribute(Version.JsonVersionConfigDeserializing, "0.9")
                .forType(ModelPojoWithVersionProperty.class)
                .readValue(JSON_VER_NOT_SPECIFIED_WITH_OLD_ATTR);
    }

    @Test (expected = JsonMappingException.class)
    public void whenJsonVersionPropertyIsSet_FailDeprecatedAttributes() throws IOException {
        mapper.reader()
                .forType(ModelPojoWithVersionProperty.class)
                .readValue(JSON_VER_09_WITH_OLD_ATTR);
    }

}
