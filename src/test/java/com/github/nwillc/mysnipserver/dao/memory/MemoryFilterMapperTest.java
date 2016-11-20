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

import com.github.nwillc.mysnipserver.dao.query.QueryGenerator;
import org.junit.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.util.function.Predicate;

public class MemoryFilterMapperTest {
    @Test
    public void testEquals() throws Exception {
        QueryGenerator<Bean> generator = new QueryGenerator<>(Bean.class).eq("value", "1");

        MemoryFilterMapper<Bean> b = new MemoryFilterMapper<>();
        generator.getFilter().accept(b);
        Predicate<Bean> predicate = b.getPredicate();
        Bean bean = new Bean();
        bean.value = "1";
        assertThat(predicate.test(bean)).isTrue();
        bean.value = "2";
        assertThat(predicate.test(bean)).isFalse();
    }

    @Test
    public void testNot() throws Exception {
        QueryGenerator<Bean> generator = new QueryGenerator<>(Bean.class).eq("value", "1").not();

        MemoryFilterMapper<Bean> b = new MemoryFilterMapper<>();
        generator.getFilter().accept(b);
        Predicate<Bean> predicate = b.getPredicate();
        Bean bean = new Bean();
        bean.value = "1";
        assertThat(predicate.test(bean)).isFalse();
        bean.value = "2";
        assertThat(predicate.test(bean)).isTrue();
    }

    @Test
    public void testAnd() throws Exception {
        QueryGenerator<Bean> generator = new QueryGenerator<>(Bean.class).eq("value", "1").eq("value2","2").and();

        MemoryFilterMapper<Bean> b = new MemoryFilterMapper<>();
        generator.getFilter().accept(b);
        Predicate<Bean> predicate = b.getPredicate();
        Bean bean = new Bean();
        bean.value = "1";
        bean.value2 = "2";
        assertThat(predicate.test(bean)).isTrue();
        bean.value2 = "3";
        assertThat(predicate.test(bean)).isFalse();
    }

    @Test
    public void testOr() throws Exception {
        QueryGenerator<Bean> generator = new QueryGenerator<>(Bean.class).eq("value", "1").eq("value","2").or();

        MemoryFilterMapper<Bean> b = new MemoryFilterMapper<>();
        generator.getFilter().accept(b);
        Predicate<Bean> predicate = b.getPredicate();
        Bean bean = new Bean();
        bean.value = "1";
        assertThat(predicate.test(bean)).isTrue();
        bean.value = "2";
        assertThat(predicate.test(bean)).isTrue();
        bean.value = "3";
        assertThat(predicate.test(bean)).isFalse();
    }

    class Bean {
        public String value;
        public String value2;
    }
}