package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import io.pengyuc.jackson.versioning.annotations.JsonUntil;
import io.pengyuc.jackson.versioning.annotations.JsonVersionProperty;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class TestJsonVersionProperty {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JsonVersioningModule());;
    private static final ContextAttributes ctxConfig =
            ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfig, "0.9");
    @JsonVersioned("1.0")
    static public class ModelPojo {
        @JsonVersionProperty
        private String version;

        @JsonUntil("0.8")
        private String deprecatedAt08 = "SomethingOld";

        @JsonUntil("0.9")
        private String deprecatedAt09 = "SomethingOld";

        private String alwaysThereAttribute = "AlwaysThere";

        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }

        public String getDeprecatedAt08() {
            return deprecatedAt08;
        }

        public String getDeprecatedAt09() {
            return deprecatedAt09;
        }

        public String getAlwaysThereAttribute() {
            return alwaysThereAttribute;
        }
    }

    @Test
    public void setTargetVersionInProperty_ShouldOverrideConfiguredVersion() throws JsonProcessingException {
        ModelPojo pojo = new ModelPojo();
        pojo.setVersion("0.8");
        String s = mapper.writer()
                .with(ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfig, "0.9"))
                .writeValueAsString(pojo);

        assertThatJson(s).node("version").isStringEqualTo("0.8");
        assertThatJson(s).node("deprecatedAt08").isAbsent();
        assertThatJson(s).node("deprecatedAt09").isPresent();
        assertThatJson(s).node("alwaysThereAttribute").isPresent();
    }

    @Test
    public void noTargetVersionInProperty_ShouldUseConfiguredVersion_ShouldShowVersionInProperty() throws JsonProcessingException {
        ModelPojo pojo = new ModelPojo();
        String s = mapper.writer()
                .with(ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfig, "0.9"))
                .writeValueAsString(pojo);

        assertThatJson(s).node("version").isStringEqualTo("0.9");
        assertThatJson(s).node("deprecatedAt08").isAbsent();
        assertThatJson(s).node("deprecatedAt09").isAbsent();
        assertThatJson(s).node("alwaysThereAttribute").isPresent();
    }

    @Test
    public void noTargetVersionInPropertyOrConfig_ShouldUseModelVersion_ShouldShowVersionInProperty() throws JsonProcessingException {
        ModelPojo pojo = new ModelPojo();
        String s = mapper.writer()
                .writeValueAsString(pojo);

        assertThatJson(s).node("version").isStringEqualTo("1.0");
        assertThatJson(s).node("deprecatedAt08").isAbsent();
        assertThatJson(s).node("deprecatedAt09").isAbsent();
        assertThatJson(s).node("alwaysThereAttribute").isPresent();
    }
}
