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
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.query.Query;
import com.github.nwillc.opa.query.QueryGenerator;
import com.mongodb.MockMongoClient;
import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class MongoDbDaoTest {
    private Fongo fongo;
    private MongoClient client;

    @Before
    public void setUp() throws Exception {
        fongo = new Fongo("mongo server 1");
        client = MockMongoClient.create(fongo);
    }

    @Test
    public void testName() throws Exception {
        assertThat(fongo).isNotNull();
        assertThat(client).isNotNull();
        MongoDbDao<String, KeyValue> dao = new MongoDbDao<>(client, KeyValue.class);
        assertThat(dao).isNotNull();

        KeyValue keyValue = new KeyValue("key", "value");

        dao.save(keyValue);
        assertThat(dao.findAll().count()).isEqualTo(1);
        Optional<KeyValue> valueOptional = dao.findOne("key");
        assertThat(valueOptional.isPresent()).isTrue();
        assertThat(valueOptional.get()).isEqualTo(keyValue);

        assertThat(valueOptional.get().getValue()).isEqualTo("value");

        keyValue.setValue("foo");
        dao.save(keyValue);
        valueOptional = dao.findOne("key");
        assertThat(valueOptional.get().getValue()).isEqualTo("foo");

        QueryGenerator<KeyValue> generator = new QueryGenerator<>(KeyValue.class);
        Query<KeyValue> query = generator.eq("value", "foo")
                .eq("key", "key")
                .and()
                .getFilter();
        assertThat(dao.find(query).count()).isEqualTo(1);
        dao.delete("key");
        assertThat(dao.findAll().count()).isEqualTo(0);
    }

    static class KeyValue extends HasKey<String> {
        private String value;

        public KeyValue() {
            this(null, null);
        }

        public KeyValue(String key, String value) {
            super(key);
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}