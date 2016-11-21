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

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Logical<T> extends Query<T> {
    private final Collection<Query<T>> queries;

    public Logical(Operator operator, Query<T> query) {
        this(operator, Collections.singletonList(query));
    }

    public Logical(Operator operator, Collection<Query<T>> queries) {
        super(operator);
        this.queries = queries;
    }

    @Override
    public void accept(QueryMapper<T> tQueryMapper) {
        queries.forEach(tFilter -> tFilter.accept(tQueryMapper));
        tQueryMapper.accept(this);
    }

    @Override
    public String toString() {
        return getOperator().name().toLowerCase() +
                '(' + queries.stream().map(Query::toString).collect(Collectors.joining(",")) + ')';
    }
}
