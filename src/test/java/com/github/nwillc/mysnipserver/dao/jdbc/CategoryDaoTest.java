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

package com.github.nwillc.mysnipserver.dao.jdbc;

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.impl.jdbc.JdbcDao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class CategoryDaoTest {
    private Dao<String, Category> categoryDao;

    @Rule
    public TestDatabase testDatabase = new TestDatabase();

    @Before
    public void setUp() throws Exception {
        categoryDao = new JdbcDao<>(new CategoryConfiguration(testDatabase.getDatabase()));
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
