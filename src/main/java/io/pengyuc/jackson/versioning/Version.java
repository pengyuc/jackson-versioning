package io.pengyuc.jackson.versioning;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Version class that can be compared. Can take multiple version numbers as sub-versions.
 */
public class Version implements Comparable<Version> {
    public static final String JsonVersionConfig = "io.pengyuc.jackson.versioning.json_version";

    private List<Integer> versionNumbers;

    @JsonCreator
    public static Version fromString(String versionStr) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionStr));
        Preconditions.checkArgument(!versionStr.startsWith("."), "Version string must not start with dot: {}", versionStr);

        List<String> subVersionStrs = Splitter.on(".").omitEmptyStrings().trimResults().splitToList(versionStr);
        List<Integer> subVersionNumbers = Lists.newArrayListWithExpectedSize(subVersionStrs.size());
        for (String subVersionStr : subVersionStrs) {
            Integer integer = Integer.valueOf(subVersionStr);
            if (integer < 0)
                throw new IllegalArgumentException("Input string value cannot be negative: " + subVersionStr);
            subVersionNumbers.add(integer);
        }
        if (subVersionNumbers.size() == 0) {
            throw new IllegalArgumentException("Cannot convert the version correctly: "+versionStr);
        }
        return new Version(subVersionNumbers);
    }

    private Version(List<Integer> versionNumbers) {
        this.versionNumbers = versionNumbers;
    }

    public int compareTo(Version other) {
        int minLength = Math.min(versionNumbers.size(), other.versionNumbers.size());

        int i=0;
        for (; i < minLength; i++) {
            int compare = versionNumbers.get(i).compareTo(other.versionNumbers.get(i));
            if (compare != 0)
                return compare;
        }
        final Integer ZERO = 0;
        if (versionNumbers.size() > minLength) {
            for (; i < versionNumbers.size();  i++)  {
                int compare = versionNumbers.get(i).compareTo(ZERO);
                if (compare != 0)
                    return compare;
            }
        } else if (other.versionNumbers.size() > minLength) {
            for (; i < other.versionNumbers.size();  i++)  {
                int compare = ZERO.compareTo(other.versionNumbers.get(i));
                if (compare != 0)
                    return compare;
            }
        }
        return 0;
    }

    @JsonValue
    public String toString() {
        return Joiner.on(".").join(versionNumbers);
    }
}
