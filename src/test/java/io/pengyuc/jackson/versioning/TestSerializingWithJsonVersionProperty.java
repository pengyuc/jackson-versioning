package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import io.pengyuc.jackson.versioning.models.ModelPojoWithVersionProperty;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class TestSerializingWithJsonVersionProperty {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JsonVersioningModule());;

    @Test
    public void setTargetVersionInProperty_ShouldOverrideConfiguredVersion() throws JsonProcessingException {
        ModelPojoWithVersionProperty pojo = new ModelPojoWithVersionProperty();
        pojo.setVersion("0.8");
        String s = mapper.writer()
                .with(ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfigSerializing, "0.9"))
                .writeValueAsString(pojo);

        assertThatJson(s).node("version").isStringEqualTo("0.8");
        assertThatJson(s).node("deprecatedAt08").isAbsent();
        assertThatJson(s).node("deprecatedAt09").isPresent();
        assertThatJson(s).node("alwaysThereAttribute").isPresent();
    }

    @Test
    public void noTargetVersionInProperty_ShouldUseConfiguredVersion_ShouldShowVersionInProperty() throws JsonProcessingException {
        ModelPojoWithVersionProperty pojo = new ModelPojoWithVersionProperty();
        String s = mapper.writer()
                .with(ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfigSerializing, "0.9"))
                .writeValueAsString(pojo);

        assertThatJson(s).node("version").isStringEqualTo("0.9");
        assertThatJson(s).node("deprecatedAt08").isAbsent();
        assertThatJson(s).node("deprecatedAt09").isAbsent();
        assertThatJson(s).node("alwaysThereAttribute").isPresent();
    }

    @Test
    public void noTargetVersionInPropertyOrConfig_ShouldUseModelVersion_ShouldShowVersionInProperty() throws JsonProcessingException {
        ModelPojoWithVersionProperty pojo = new ModelPojoWithVersionProperty();
        String s = mapper.writer()
                .writeValueAsString(pojo);

        assertThatJson(s).node("version").isStringEqualTo("1.0");
        assertThatJson(s).node("deprecatedAt08").isAbsent();
        assertThatJson(s).node("deprecatedAt09").isAbsent();
        assertThatJson(s).node("alwaysThereAttribute").isPresent();
    }
}
