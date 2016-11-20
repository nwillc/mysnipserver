/*
 * Copyright (c) 2016, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package com.github.nwillc.mysnipserver.dao.query;

import com.github.nwillc.mysnipserver.util.Accessor;

import java.util.function.Function;

public class Comparison<T> implements Filter<T> {
    private final String value;
    private final String fieldName;
    private final Function<T, String> accessor;
    private final Operator operator;

    public Comparison(Class<T> tClass, String fieldName, String value, Operator operator) throws NoSuchFieldException {
        accessor = Accessor.getFunction(fieldName, tClass);
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    public Function<T, String> getAccessor() {
        return accessor;
    }

    public String getFieldName() { return fieldName; }
    public String getValue() {
        return value;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator.name().toLowerCase() + "(\"" + getFieldName() + "\",\"" + getValue() + "\")";
    }
}
