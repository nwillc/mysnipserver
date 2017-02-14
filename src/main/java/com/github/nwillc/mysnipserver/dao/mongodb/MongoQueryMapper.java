/*
 * Copyright (c) 2017, nwillc@gmail.com
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
 */

package com.github.nwillc.mysnipserver.dao.mongodb;

import com.github.nwillc.opa.query.Comparison;
import com.github.nwillc.opa.query.Query;
import com.github.nwillc.opa.query.QueryMapper;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayDeque;
import java.util.Deque;

public class MongoQueryMapper<T> implements QueryMapper<T> {
    private Deque<Bson> bsons = new ArrayDeque<>();

    @Override
    public void accept(Query<T> tQuery) {
        String fieldName, value;
        Bson bson;

        switch (tQuery.getOperator()) {
            case EQ:
                fieldName = ((Comparison<T>) tQuery).getFieldName();
                value = ((Comparison<T>) tQuery).getValue();
                bsons.addLast(Filters.eq(fieldName, value));
                break;
            case CONTAINS:
                fieldName = ((Comparison<T>) tQuery).getFieldName();
                value = ((Comparison<T>) tQuery).getValue();
                bsons.addLast(Filters.regex(fieldName, ".*" + value + ".*", "i"));
                break;
            case NOT:
                bson = bsons.removeLast();
                bsons.addLast(Filters.not(bson));
                break;
            case AND:
                bson = Filters.and(bsons);
                bsons = new ArrayDeque<>();
                bsons.addLast(bson);
                break;
            case OR:
                bson = Filters.or(bsons);
                bsons = new ArrayDeque<>();
                bsons.addLast(bson);
                break;
        }
    }

    public Bson toBson() {
        return bsons.getFirst();
    }
}
