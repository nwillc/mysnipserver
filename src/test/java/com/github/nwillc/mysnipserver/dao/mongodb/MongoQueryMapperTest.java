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


import com.github.fakemongo.Fongo;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa_impl_tests.OpaImplTest;
import com.github.nwillc.opa_impl_tests.QueryMapperTest;
import com.mongodb.MockMongoClient;
import com.mongodb.MongoClient;

public class MongoQueryMapperTest extends QueryMapperTest {
    private Fongo fongo;
    private MongoClient client;

    @Override
    protected Dao<String, OpaImplTest.TestEntity> getDao() {
        Fongo fongo = new Fongo("mongo server 1");
        MongoClient client = MockMongoClient.create(fongo);

        return new MongoDbDao<>(client, OpaImplTest.TestEntity.class);
    }
}