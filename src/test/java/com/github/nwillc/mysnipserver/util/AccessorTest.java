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

package com.github.nwillc.mysnipserver.util;

import com.github.nwillc.contracts.UtilityClassContract;
import com.github.nwillc.mysnipserver.entity.Entity;
import org.junit.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class AccessorTest extends UtilityClassContract {

    @Override
    public Class<?> getClassToTest() {
        return Accessor.class;
    }

    @Test
    public void getFunction() throws Exception {
        final Bean bean = new Bean("foo");

        assertThat(bean.getValue()).isEqualTo("foo");
        Function<Bean,String> accessor = Accessor.getFunction("value", Bean.class);
        assertThat(accessor.apply(bean)).isEqualTo("foo");
    }

    @Test
    public void getSuperFunction() throws Exception {
        final Bean bean = new Bean("foo");

        Function<Bean,String> accessor = Accessor.getFunction("key", Bean.class);
        assertThat(accessor).isNotNull();
        assertThat(accessor.apply(bean)).isEqualTo(bean.getKey());
    }

    public static class Bean extends Entity {
        private final String value;

        public Bean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}