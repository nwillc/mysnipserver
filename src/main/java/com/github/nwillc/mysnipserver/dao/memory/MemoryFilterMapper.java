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

import com.github.nwillc.mysnipserver.dao.query.EqFilter;
import com.github.nwillc.mysnipserver.dao.query.Filter;
import com.github.nwillc.mysnipserver.dao.query.FilterMapper;
import com.github.nwillc.mysnipserver.dao.query.KVFilter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.function.Predicate;

public class MemoryFilterMapper<T> implements FilterMapper<T> {
    final Deque<Predicate<T>> predicates = new ArrayDeque<>();

    @Override
    @SuppressWarnings("unchecked")
    public void accept(Filter<T> tFilter) {
        if (tFilter instanceof KVFilter) {
            final String value = ((KVFilter) tFilter).getValue();
            final Function<T,String> accessor = ((KVFilter) tFilter).getAccessor();

            if (tFilter instanceof EqFilter) {
               predicates.addLast(t -> accessor.apply(t).equals(value));
            } else {
               predicates.addLast(t -> accessor.apply(t).contains(value));
            }
        } else {
           switch (tFilter.getClass().getSimpleName()) {
               case "NotFilter":
                   predicates.addLast(t -> !predicates.getLast().test(t));
                   break;
           }
        }
    }

    public Predicate<T> getPredicate() {
        return predicates.getFirst();
    }
}
