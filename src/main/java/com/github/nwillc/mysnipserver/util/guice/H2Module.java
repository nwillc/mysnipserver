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

package com.github.nwillc.mysnipserver.util.guice;

import com.github.nwillc.mysnipserver.dao.jdbc.CategoryConfiguration;
import com.github.nwillc.mysnipserver.dao.jdbc.JdbcDatabase;
import com.github.nwillc.mysnipserver.dao.jdbc.SnippetConfiguration;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.mysnipserver.handlers.GraphQLHandler;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.impl.jdbc.JdbcDao;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import org.pmw.tinylog.Logger;


public class H2Module implements Module {
    private static final String H2_DATABASE_NAME = "./db/mysnips";

    @Override
    public void configure(Binder binder) {
        Logger.info("DI Module: H2 Backed");

        final JdbcDatabase h2Database;
        try {
            h2Database = new JdbcDatabase(H2_DATABASE_NAME);
        } catch (Exception e) {
            Logger.error("Failed creating h2 database " + H2_DATABASE_NAME, e);
            throw new IllegalStateException("Database not available", e);
        }

        final JdbcDao<String, Category> categoryJdbcDao = new JdbcDao<>(new CategoryConfiguration(h2Database));
        final JdbcDao<String, Snippet> snippetJdbcDao = new JdbcDao<>(new SnippetConfiguration(h2Database));
        final GraphQLHandler graphQLHandlerV2 = new GraphQLHandler(categoryJdbcDao, snippetJdbcDao);

        binder.bind(GraphQLHandler.class).toInstance(graphQLHandlerV2);
        binder.bind(new TypeLiteral<Dao<String, Category>>() {
        }).toInstance(categoryJdbcDao);
        binder.bind(new TypeLiteral<Dao<String, Snippet>>() {
        }).toInstance(snippetJdbcDao);
    }
}
