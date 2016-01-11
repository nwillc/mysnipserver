/*
 * Copyright (c) 2016,  nwillc@gmail.com
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

package com.github.nwillc.mysnipserver.util.http;

import com.github.nwillc.contracts.UtilityClassContract;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpUtilsTest extends UtilityClassContract {
    private static final String TEST_PATH = "test";
    private static final int TEST_PORT = 7654;
    private static final String TEST_URL = "http://localhost:" + TEST_PORT + "/" + TEST_PATH;
    private static final String TEST_RESULT = "Hello";
    private static final String TEST_PARAM_NAME = "payload";
    private static final String TEST_PARAM_VALUE = "World";

    @Override
    public Class<?> getClassToTest() {
        return HttpUtils.class;
    }

    @Before
    public void setUp() throws Exception {
        Spark.port(TEST_PORT);
        Spark.post(TEST_PATH, (req, res) -> TEST_RESULT + req.body().split("=")[1]);
        Spark.awaitInitialization();
    }

    @After
    public void tearDown() throws Exception {
        Spark.stop();
    }

    @Test
    public void shouldHttpPost() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(TEST_PARAM_NAME, TEST_PARAM_VALUE);
        assertThat(HttpUtils.httpPost(TEST_URL, params)).isEqualTo(TEST_RESULT + TEST_PARAM_VALUE);
    }

    @Test
    public void testAppUrl() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localtest:4567/v3/test"));
        when(request.getRequestURI()).thenReturn("/v3/test");
        assertThat(HttpUtils.appUrl(request)).isEqualTo("http://localtest:4567");
    }
}