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
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AndFilter<T extends Entity> implements Filter<T> {
    private final Collection<Filter<T>> filters;

    public AndFilter(Collection<Filter<T>> filters) {
        this.filters = new ArrayList<>(filters);
    }

    @Override
    public Predicate<T> toPredicate() {
        Predicate<T> result = null;
        for (Filter<T> filter : filters) {
            result = (result == null) ? filter.toPredicate() : result.and(filter.toPredicate());
        }
        return result;
    }

    @Override
    public Bson toBson() {
        return Filters.and(filters.stream().map(Filter::toBson).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "and(" + filters.stream().map(Filter::toString).collect(Collectors.joining(",")) + ')';
    }
}
