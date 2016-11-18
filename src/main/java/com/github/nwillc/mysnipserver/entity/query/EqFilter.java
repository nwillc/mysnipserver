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

package com.github.nwillc.mysnipserver.entity.query;


import com.github.nwillc.mysnipserver.entity.Entity;
import com.github.nwillc.mysnipserver.util.Accessor;

import java.util.function.Function;
import java.util.function.Predicate;

public class EqFilter<T extends Entity> implements Filter<T> {
    private final String fieldName;
    private final String value;
    private final Function<T, String> function;

    public EqFilter(final Class<T> tClass, String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
        function = Accessor.getFunction(fieldName, tClass);
    }

    @Override
    public Predicate<T> toPredicate() {
        return t -> function.apply(t).equals(value);
    }

    @Override
    public String toString() {
        return "eq(\"" + fieldName + "\",\"" + value + "\")";
    }
}