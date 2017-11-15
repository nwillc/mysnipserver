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

package com.github.nwillc.mysnipserver.dao.h2;


import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.impl.jdbc.JdbcDao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SnippetDaoTest {
    private Dao<String, Snippet> snippetDao;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        folder.create();
        final File folderRoot = folder.getRoot();
        final JdbcDatabase h2Database = new JdbcDatabase(folderRoot.getPath() + File.separator + "snippets");
        snippetDao = new JdbcDao<>(new SnippetConfiguration(h2Database));
    }

    @Test
    public void testRetrieve() throws Exception {
        final Snippet snippet = new Snippet("foo", "bar", "baz");
        snippetDao.save(snippet);
        final Optional<Snippet> one = snippetDao.findOne(snippet.getKey());
        assertThat(one.isPresent()).isTrue();
    }

    @Test
    public void testDelete() throws Exception {
        final Snippet snippet = new Snippet("foo", "bar", "baz");
        snippetDao.save(snippet);
        final Optional<Snippet> one = snippetDao.findOne(snippet.getKey());
        assertThat(one.isPresent()).isTrue();
        snippetDao.delete(snippet.getKey());
        final Optional<Snippet> two = snippetDao.findOne(snippet.getKey());
        assertThat(two.isPresent()).isFalse();
    }

    @Test
    public void testUpdate() throws Exception {
        final Snippet snippet = new Snippet("foo", "bar", "baz");
        snippetDao.save(snippet);
        final Optional<Snippet> one = snippetDao.findOne(snippet.getKey());
        assertThat(one.isPresent()).isTrue();
        snippet.setCategory("foo2");
        snippet.setTitle("bar2");
        snippet.setBody("baz2");
        snippetDao.save(snippet);
        final Optional<Snippet> two = snippetDao.findOne(snippet.getKey());
        assertThat(two.isPresent()).isTrue();
        final Snippet snippetTwo = two.get();
        assertThat(snippetTwo.getCategory()).isEqualTo(snippet.getCategory());
        assertThat(snippetTwo.getBody()).isEqualTo(snippet.getBody());
        assertThat(snippetTwo.getTitle()).isEqualTo(snippet.getTitle());
    }
}