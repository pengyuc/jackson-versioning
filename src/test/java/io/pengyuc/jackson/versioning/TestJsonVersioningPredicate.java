package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.google.common.base.Predicate;
import io.pengyuc.jackson.versioning.annotations.JsonSince;
import io.pengyuc.jackson.versioning.annotations.JsonUntil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestJsonVersioningPredicate {
    static Predicate<BeanProperty> predicate;
    @BeforeClass
    static public void setupClass() {
        predicate = JsonVersioningPredicate.forPropertyInVersion(Version.fromString("1.0"));
    }

    private PropertyWriter mockVersioning(String since, String until) {
        PropertyWriter mock = mock(PropertyWriter.class);
        if (since != null) {
            JsonSince mockSince = mock(JsonSince.class);
            when(mockSince.value()).thenReturn(since);
            when(mock.getAnnotation(JsonSince.class)).thenReturn(mockSince);
        }
        if (until != null) {
            JsonUntil mockUntil = mock(JsonUntil.class);
            when(mockUntil.value()).thenReturn(until);
            when(mock.getAnnotation(JsonUntil.class)).thenReturn(mockUntil);
        }
        return mock;
    }

    @Test
    public void whenNoVersioning_ShallAlwaysPass() {
        Assert.assertTrue(predicate.apply(mockVersioning(null, null)));
    }

    @Test
    public void untilVersionGreaterThanTargetVersion_Pass() {
        Assert.assertTrue(predicate.apply(mockVersioning(null, "1.1")));
    }

    @Test
    public void untilVersionSmallerThanOrEqualToTargetVersion_Fail() {
        Assert.assertFalse(predicate.apply(mockVersioning(null, "0.9")));
        Assert.assertFalse(predicate.apply(mockVersioning(null, "1.0")));
    }

    @Test
    public void sinceVersionGreaterThanTargetVersion_Fail() {
        Assert.assertFalse(predicate.apply(mockVersioning("1.1", null)));
    }

    @Test
    public void sinceVersionSmallerThanOrEqualToTargetVersion_Pass() {
        Assert.assertTrue(predicate.apply(mockVersioning("0.9", null)));
        Assert.assertTrue(predicate.apply(mockVersioning("1.0", null)));
    }

    @Test
    public void targetVersionBetweenSinceAndUntil_Pass() {
        Assert.assertTrue(predicate.apply(mockVersioning("0.9", "1.1")));
    }

    @Test
    public void targetVersionSmallerThanSinceAndUntil_Fail() {
        Assert.assertFalse(predicate.apply(mockVersioning("2.0", "3.0")));
    }

    @Test
    public void targetVersionGreaterThanOrEqualToSinceAndUntil_Fail() {
        Assert.assertFalse(predicate.apply(mockVersioning("0.8", "0.9")));
        Assert.assertFalse(predicate.apply(mockVersioning("0.8", "1.0")));
        Assert.assertFalse(predicate.apply(mockVersioning("1.0", "1.0")));
    }


    @Test
    public void targetVersionBetweenReversedSinceAndUntil_Fail() {
        Assert.assertFalse(predicate.apply(mockVersioning("1.1", "0.9")));
        Assert.assertFalse(predicate.apply(mockVersioning("1.1", "1.0")));
    }

    @Test
    public void targetVersionSmallerThanReversedSinceAndUntil_Pass() {
        Assert.assertTrue(predicate.apply(mockVersioning("2.0", "1.1")));
    }

    @Test
    public void targetVersionGreaterThanOrEqualsToReversedSinceAndUntil_Pass() {
        Assert.assertTrue(predicate.apply(mockVersioning("0.8", "0.5")));
        Assert.assertTrue(predicate.apply(mockVersioning("1.0", "0.5")));
    }

}
