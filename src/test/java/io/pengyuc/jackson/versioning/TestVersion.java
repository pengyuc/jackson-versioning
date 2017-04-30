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

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the {@link Version} class
 */
public class TestVersion {
    @Test
    public void versionCanBeSingleDigit() {
        Version version = Version.fromString("1");
        Assert.assertEquals("1", version.toString());
    }

    @Test
    public void versionCanMultipleSubVersions() {
        Version version = Version.fromString("1.2.3.4.5");
        Assert.assertEquals("1.2.3.4.5", version.toString());
    }

    @Test
    public void versionCanIgnoreDoubleDots() {
        Version version = Version.fromString("1..2.3...4.5");
        Assert.assertEquals("1.2.3.4.5", version.toString());
    }

    @Test
    public void versionCanIgnoreLastDot() {
        Version version = Version.fromString("1.2.");
        Assert.assertEquals("1.2", version.toString());
    }

    @Test (expected = NumberFormatException.class)
    public void versionMustBeAbleToConvertToInteger() {
        Version.fromString("1.and2");
    }

    @Test (expected = IllegalArgumentException.class)
    public void versionNumberMustBePositive() {
        Version.fromString("1.-2");
    }

    @Test (expected = IllegalArgumentException.class)
    public void versionMustNotStartWithDot() {
        Version.fromString(".1");
    }

    @Test
    public void versionCompareLikeTheNumber() {
        Assert.assertEquals(0, Version.fromString("1").compareTo(Version.fromString("1")));
        Assert.assertEquals(-1, Version.fromString("1").compareTo(Version.fromString("2")));
        Assert.assertEquals(1, Version.fromString("1").compareTo(Version.fromString("0")));
    }

    @Test
    public void versionCompareSubVersionNumber() {
        Assert.assertEquals(0, Version.fromString("1.1").compareTo(Version.fromString("1.1")));
        Assert.assertEquals(-1, Version.fromString("1.1").compareTo(Version.fromString("1.2")));
        Assert.assertEquals(1, Version.fromString("1.1").compareTo(Version.fromString("1.0")));
    }

    @Test
    public void versionCompareMajorVersionNumberFirst() {
        Assert.assertEquals(0, Version.fromString("1.1").compareTo(Version.fromString("1.1")));
        Assert.assertEquals(-1, Version.fromString("0.1").compareTo(Version.fromString("1.0")));
        Assert.assertEquals(1, Version.fromString("2.1").compareTo(Version.fromString("1.2")));
    }

    @Test
    public void versionCompareOmittedNumberAsZero() {
        Assert.assertEquals(-1, Version.fromString("1").compareTo(Version.fromString("1.1")));
        Assert.assertEquals(-1, Version.fromString("1").compareTo(Version.fromString("1.0.0.1")));
        Assert.assertEquals(0, Version.fromString("1.0.0").compareTo(Version.fromString("1.0")));
        Assert.assertEquals(1, Version.fromString("1.2.0.1").compareTo(Version.fromString("1.2")));
    }

}
