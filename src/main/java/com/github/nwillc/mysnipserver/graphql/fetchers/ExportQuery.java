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

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Export;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.opa.Dao;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportQuery implements DataFetcher<Export> {
    private final Dao<String, Snippet> snippetDao;
    private final Dao<String, Category> categoryDao;

    public ExportQuery(Dao<String, Snippet> snippetDao, Dao<String, Category> categoryDao) {
        this.snippetDao = snippetDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public Export get(DataFetchingEnvironment environment) {
       return new Export() {
            @Override
            public List<Category> getCategories() {
                try (Stream<Category> categoryStream = categoryDao.findAll()) {
                    return categoryStream.collect(Collectors.toList());
                }
            }

            @Override
            public List<Snippet> getSnippets() {
                try (Stream<Snippet> snippetStream = snippetDao.findAll()) {
                    return snippetStream.collect(Collectors.toList());
                }
            }
        };
    }
}
