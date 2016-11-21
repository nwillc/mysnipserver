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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class QueryGenerator<T> {
    private Deque<Query<T>> queries = new ArrayDeque<>();
    private final Class<T> tClass;

    public QueryGenerator(Class<T> tClass) {
        this.tClass = tClass;
    }

    public QueryGenerator<T> contains(String key, String value) throws NoSuchFieldException {
        queries.addLast(new Comparison<>(tClass, key, value, Operator.CONTAINS));
        return this;
    }

    public QueryGenerator<T> eq(String key, String value) throws NoSuchFieldException {
        queries.addLast(new Comparison<>(tClass, key, value, Operator.EQ));
        return this;
    }

    public QueryGenerator<T> not() {
        queries.addFirst(new Logical<>(Operator.NOT, queries.removeFirst()));
        return this;
    }

    public QueryGenerator<T> and() {
        Query<T> and = new Logical<>(Operator.AND, queries);
        queries = new ArrayDeque<>();
        queries.addFirst(and);
        return this;
    }

    public QueryGenerator<T> or() {
        Query<T> and = new Logical<>(Operator.OR, queries);
        queries = new ArrayDeque<>();
        queries.addFirst(and);
        return this;
    }

    public Query<T> getFilter() {
        if (queries.isEmpty()) {
            return null;
        }

        if (queries.size() == 1) {
            return queries.getFirst();
        }

        return new Logical<>(Operator.OR, queries);
    }

    @Override
    public String toString() {
        return queries.stream().map(Query::toString).collect(Collectors.joining(", "));
    }

}
