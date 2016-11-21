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

package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.mysnipserver.dao.query.Query;
import com.github.nwillc.mysnipserver.dao.query.QueryMapper;
import com.github.nwillc.mysnipserver.dao.query.Comparison;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.function.Predicate;

public class MemoryQueryMapper<T> implements QueryMapper<T> {
    final Deque<Predicate<T>> predicates = new ArrayDeque<>();

    @Override
    @SuppressWarnings("unchecked")
    public void accept(Query<T> tQuery) {
        Predicate<T> one, two;
        Function<T, String> accessor;
        String value;

        switch (tQuery.getOperator()) {
            case EQ:
                accessor = ((Comparison) tQuery).getAccessor();
                value = ((Comparison) tQuery).getValue();
                predicates.addLast(t -> accessor.apply(t).equals(value));
                break;
            case CONTAINS:
                accessor = ((Comparison) tQuery).getAccessor();
                value = ((Comparison) tQuery).getValue();
                predicates.addLast(t -> accessor.apply(t).contains(value));
                break;
            case NOT:
                one = predicates.removeLast();
                predicates.addLast(t -> !one.test(t));
                break;
            case AND:
                one = predicates.removeLast();
                two = predicates.removeLast();
                predicates.addLast(t -> one.test(t)
                        && two.test(t));
                break;
            case OR:
                one = predicates.removeLast();
                two = predicates.removeLast();
                predicates.addLast(t -> one.test(t) || two.test(t));
                break;
        }
    }

    public Predicate<T> getPredicate() {
        return predicates.getFirst();
    }
}
