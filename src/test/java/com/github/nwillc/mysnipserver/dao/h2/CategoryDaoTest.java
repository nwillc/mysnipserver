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

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.impl.jdbc.JdbcDao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class CategoryDaoTest {
    private Dao<String, Category> categoryDao;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        folder.create();
        final File folderRoot = folder.getRoot();
        final H2Database h2Database = new H2Database(folderRoot.getPath() + File.separator + "snippets");
        categoryDao = new JdbcDao<>(new CategoryConfiguration(h2Database));
    }

    @Test
    public void testRetrieve() throws Exception {
        final Category category = new Category("foo");
        categoryDao.save(category);
        final Optional<Category> one = categoryDao.findOne(category.getKey());
        assertThat(one.isPresent()).isTrue();
    }

    @Test
    public void testDelete() throws Exception {
        final Category category = new Category("foo");
        categoryDao.save(category);
        final Optional<Category> one = categoryDao.findOne(category.getKey());
        assertThat(one.isPresent()).isTrue();
        categoryDao.delete(category.getKey());
        final Optional<Category> two = categoryDao.findOne(category.getKey());
        assertThat(two.isPresent()).isFalse();
    }

    @Test
    public void testUpdate() throws Exception {
        final Category category = new Category("foo");
        categoryDao.save(category);
        final Optional<Category> one = categoryDao.findOne(category.getKey());
        assertThat(one.get().getName()).isEqualTo("foo");
        category.setName("bar");
        categoryDao.save(category);
        final Optional<Category> two = categoryDao.findOne(category.getKey());
        assertThat(two.get().getName()).isEqualTo("bar");
    }
}
