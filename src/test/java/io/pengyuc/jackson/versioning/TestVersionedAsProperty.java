package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
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
    private static ContextAttributes ctxVersion1;

    @BeforeClass
    static public void setupClass() {
        mapper = new ObjectMapper().registerModule(new JsonVersioningModule());
        ctxVersion1 = ContextAttributes.getEmpty().withPerCallAttribute(Version.JsonVersionConfig, "1.0");
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
        String s  =mapper.writer().with(ctxVersion1).writeValueAsString(model);

        assertThatJson(s).node("car.capacity").isAbsent();
        assertThatJson(s).node("car.model").isPresent();
    }

}
