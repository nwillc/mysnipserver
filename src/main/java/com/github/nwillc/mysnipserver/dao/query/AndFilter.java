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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class AndFilter<T> implements Filter<T> {
    private final Collection<Filter<T>> filters;

    public AndFilter(Collection<Filter<T>> filters) {
        this.filters = new ArrayList<>(filters);
    }

    @Override
    public void accept(FilterMapper<T> tFilterMapper) {
        filters.forEach(tFilter -> tFilter.accept(tFilterMapper));
        tFilterMapper.accept(this);
    }

    @Override
    public String toString() {
        return "and(" + filters.stream().map(Filter::toString).collect(Collectors.joining(",")) + ')';
    }
}
