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

package com.github.nwillc.mysnipserver.graphql.fetchers;

import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.opa.Dao;
import graphql.schema.DataFetchingEnvironment;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(JMockit.class)
public class SnippetFetcherTest {
    private SnippetFetcher instance;

    @Mocked
    private Dao<String,Snippet> dao;

    @Mocked
    private DataFetchingEnvironment env;

    @Before
    public void setUp() throws Exception {
        instance = new SnippetFetcher(dao);
        assertThat(instance).isNotNull();
    }

    @Test
    public void testGetSnippet() throws Exception {
        final Snippet snippet = new Snippet();

        new Expectations(){{
            env.getArgument("key"); result = "foo";
            dao.findOne("foo"); result = Optional.of(snippet);
        }};

        final Snippet snippet1 = instance.get(env);
        assertThat(snippet1).isEqualTo(snippet);
    }

    @Test
    public void testGetUnknownSnippet() throws Exception {
        new Expectations(){{
            env.getArgument("key"); result = "foo";
            dao.findOne("foo"); result = Optional.empty();
        }};

        assertThat(instance.get(env)).isNull();
    }
}