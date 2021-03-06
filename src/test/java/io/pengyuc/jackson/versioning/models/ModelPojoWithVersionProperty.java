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

package io.pengyuc.jackson.versioning.models;

import io.pengyuc.jackson.versioning.annotations.JsonUntil;
import io.pengyuc.jackson.versioning.annotations.JsonVersionProperty;
import io.pengyuc.jackson.versioning.annotations.JsonVersioned;
@JsonVersioned("1.0")
public class ModelPojoWithVersionProperty {
    @JsonVersionProperty
    private String version;

    @JsonUntil("0.8")
    private String deprecatedAt08;

    @JsonUntil("0.9")
    private String deprecatedAt09;

    private String alwaysThereAttribute;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeprecatedAt08() {
        return deprecatedAt08;
    }

    public void setDeprecatedAt08(String deprecatedAt08) {
        this.deprecatedAt08 = deprecatedAt08;
    }

    public String getDeprecatedAt09() {
        return deprecatedAt09;
    }

    public void setDeprecatedAt09(String deprecatedAt09) {
        this.deprecatedAt09 = deprecatedAt09;
    }

    public String getAlwaysThereAttribute() {
        return alwaysThereAttribute;
    }

    public void setAlwaysThereAttribute(String alwaysThereAttribute) {
        this.alwaysThereAttribute = alwaysThereAttribute;
    }
}
