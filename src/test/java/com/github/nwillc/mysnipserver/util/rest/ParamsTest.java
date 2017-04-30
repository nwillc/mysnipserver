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

package com.github.nwillc.mysnipserver.util.rest;


import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ParamsTest {
    private List<String> values = asList("PASSWORD", "USERNAME", "TOKEN");
    private List<String> labels = values.stream().map(s -> ":" + s.toLowerCase()).collect(Collectors.toList());

    @Test
    public void testValueOf() throws Exception {
        for (String value : values) {
            assertThat(Params.valueOf(value)).isNotNull();
        }
    }

    @Test
    public void testGetLabel() throws Exception {
        for (Params param : Params.values()) {
            assertThat(labels).contains(param.getLabel());
        }
    }

    @Test
    public void testOf() throws Exception {
        final String newPath = Params.PASSWORD.of("path");
        assertThat(newPath).isEqualTo("path/:password");
    }

}