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
