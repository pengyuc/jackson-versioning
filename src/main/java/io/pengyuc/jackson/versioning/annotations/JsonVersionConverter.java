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

package io.pengyuc.jackson.versioning.annotations;

import java.lang.annotation.*;

/**
 * A converter method that convert the model between versions. This annotation can be used with {@link JsonSince} and
 * {@link JsonUntil} to limit the invocation versions of the JSON.
 * The converter method shall manipulate the JsonNode object in this order:
 * Deserializing:
 * Input String -> JsonNode -> other versioning annotations; ex: {@link JsonSince} and/or {@link JsonUntil}
 *              -> converter -> versioned JsonNode -> Jackson deserialize into a model instance.
 * Serializing:
 * Model instance -> Jackson deserialize into JsonNode -> other versioning annotations
 *              -> converter -> Jackson serialize into string
 *
 * If there are multiple converter, the order of their execution is not guaranteed.
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonVersionConverter {
}
