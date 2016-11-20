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

package com.github.nwillc.mysnipserver.dao.mongodb;

import com.github.nwillc.mysnipserver.dao.query.EqFilter;
import com.github.nwillc.mysnipserver.dao.query.Filter;
import com.github.nwillc.mysnipserver.dao.query.FilterMapper;
import com.github.nwillc.mysnipserver.dao.query.KVFilter;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayDeque;
import java.util.Deque;

public class MongoFilterMapper<T> implements FilterMapper<T> {
    final Deque<Bson> bsons = new ArrayDeque<>();

    @Override
    public void accept(Filter<T> tFilter) {
        if (tFilter instanceof KVFilter) {
            final String fieldName = ((KVFilter) tFilter).getFieldName();
            final String value = ((KVFilter) tFilter).getValue();

            if (tFilter instanceof EqFilter) {
                bsons.addLast(Filters.eq(fieldName, value));
            } else {
                bsons.addLast(Filters.regex(fieldName, ".*" + value + ".*", "i"));
            }
        } else {
            final Bson bson;
            switch (tFilter.getClass().getSimpleName()) {
                case "NotFilter":
                    bson = bsons.removeLast();
                    bsons.addLast(Filters.not(bson));
                    break;
                case "AndFilter":
                    bson = Filters.and(bsons);
                    bsons.clear();
                    bsons.addLast(bson);
                    break;
                case "OrFilter":
                    bson = Filters.or(bsons);
                    bsons.clear();
                    bsons.addLast(bson);
                    break;
            }
        }
    }

    public Bson toBson() {
        return bsons.getFirst();
    }
}
