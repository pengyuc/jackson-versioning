package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
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
                .with(ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfigSerializing, "0.9"))
                .writeValueAsString(pojo);

        assertThatJson(s).node(ATTR_VERSION).isStringEqualTo("0.8");
        assertThatJson(s).node(ATTR_DEPRECATED_AT_08).isAbsent();
        assertThatJson(s).node(ATTR_DEPRECATED_AT_09).isPresent();
        assertThatJson(s).node(ATTR_ALWAYS_THERE_ATTRIBUTE).isPresent();
    }

    @Test
    public void noTargetVersionInProperty_ShouldUseConfiguredVersion_ShouldShowVersionInProperty() throws JsonProcessingException {
        String s = writer
                .with(ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfigSerializing, "0.9"))
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
