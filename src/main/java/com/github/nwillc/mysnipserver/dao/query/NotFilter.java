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

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.function.Predicate;

public class NotFilter<T> implements Filter<T> {
    private final Filter<T> filter;

    public NotFilter(Filter<T> filter) {
        this.filter = filter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Predicate<T> toPredicate() {
        return filter.toPredicate().negate();
    }

    @Override
    public void accept(FilterMapper<T> tFilterMapper) {
        filter.accept(tFilterMapper);
        tFilterMapper.accept(this);
    }

    @Override
    public Bson toBson() {
        return Filters.not(filter.toBson());
    }

    @Override
    public String toString() {
        return "not(" + filter + ')';
    }
}
