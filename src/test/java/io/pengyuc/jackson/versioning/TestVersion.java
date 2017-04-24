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
