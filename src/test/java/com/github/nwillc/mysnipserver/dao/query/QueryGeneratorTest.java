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

import com.github.nwillc.mysnipserver.entity.Entity;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QueryGeneratorTest {
    private QueryGenerator<Bean> queryGenerator;

    @Before
    public void setUp() throws Exception {
        queryGenerator = new QueryGenerator<>(Bean.class);
    }

    @Test
    public void testEq() throws Exception {
        QueryGenerator generator = queryGenerator
                .contains("key", "42");
        assertThat(generator.toString()).isEqualTo("regex(\"key\",\".*42.*\",\"i\")");
    }

    @Test
    public void testNot() throws Exception {
        QueryGenerator generator = queryGenerator
                .contains("key","1")
                .not();
        assertThat(generator.toString()).isEqualTo("not(regex(\"key\",\".*1.*\",\"i\"))");
    }

    @Test
    public void testAnd() throws Exception {
        QueryGenerator generator = queryGenerator
                .contains("key","1")
                .contains("key","2")
                .and();
        assertThat(generator.toString()).isEqualTo("and(regex(\"key\",\".*1.*\",\"i\"),regex(\"key\",\".*2.*\",\"i\"))");

    }

    @Test
    public void testOr() throws Exception {
        QueryGenerator generator = queryGenerator
                .contains("key","1")
                .contains("key","2")
                .or();
        assertThat(generator.toString()).isEqualTo("or(regex(\"key\",\".*1.*\",\"i\"),regex(\"key\",\".*2.*\",\"i\"))");

    }

    public class Bean extends Entity {
        private final String value;
        private String second;

        public Bean(String key, String value) {
            super(key);
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "key='" + getKey() + '\'' +
                    " value='" + value + '\'' +
                    ", second='" + second + '\'' +
                    '}';
        }
    }
}