/*
 * Copyright (c) 2015,  nwillc@gmail.com
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

package com.github.nwillc.mysnipserver.util;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ToJsonTest implements ToJson {

    @Test
    public void testToJson() throws Exception {
        Sample sample = new Sample(1L,"one", true, 1, 2, 3);

        assertThat(sample).isNotNull();
        assertThat(toJson(sample)).isEqualTo("{\"number\":1,\"str\":\"one\",\"flag\":true,\"dead\":null,\"bits\":[1,2,3]}");
    }

    static class Sample {
        final public Long number;
        final public String str;
        final public Boolean flag;
        final public Object dead = null;
        final public int[] bits;

        public Sample(Long number, String str, Boolean flag, int ... bits) {
            this.number = number;
            this.str = str;
            this.flag = flag;
            this.bits = bits;
        }
    }
}
