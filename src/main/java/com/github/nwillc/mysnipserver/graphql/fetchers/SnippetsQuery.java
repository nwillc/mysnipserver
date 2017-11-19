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

import com.github.nwillc.funjdbc.UncheckedSQLException;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.query.Query;
import com.github.nwillc.opa.query.QueryBuilder;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnippetsQuery extends DaoFetcher<String, Snippet, List<Snippet>> {
    public SnippetsQuery(Dao<String, Snippet> dao) {
        super(dao);
    }

    @Override
    public List<Snippet> get(DataFetchingEnvironment environment) {
        final String category = environment.getArgument("category");
        final String match = environment.getArgument("match");
        QueryBuilder<Snippet> queryBuilder = null;

        try {
            if (category != null) {
                queryBuilder = new QueryBuilder<>(Snippet.class).eq("category", category);
            }

            if (match != null) {
                boolean and = queryBuilder != null;
                if (!and) {
                    queryBuilder = new QueryBuilder<>(Snippet.class);
                }

                queryBuilder = queryBuilder.contains("title", match);
                if (and) {
                    queryBuilder = queryBuilder.and();
                }
            }
        } catch (NoSuchFieldException e) {
            throw new UncheckedSQLException("Cant build query", e);
        }

        final Query<Snippet> query = queryBuilder == null ? null : queryBuilder.build();
        try (final Stream<Snippet> snippets = query == null ? getDao().findAll() : getDao().find(query)) {
            return snippets.collect(Collectors.toList());
        }
    }
}
