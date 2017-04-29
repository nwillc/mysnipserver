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

package com.github.nwillc.mysnipserver.handlers;


import com.github.nwillc.mysnipserver.DaoProvider;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.opa.Dao;
import com.google.inject.Inject;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class GraphQL implements Handler, DaoProvider {
    private final Dao<String, Category> categoriesDao;
    private final Dao<String, Snippet> snippetDao;

    @Inject
    public GraphQL(Dao<String, Category> categoriesDao,
                   Dao<String, Snippet> snippetDao) {
        this.categoriesDao = categoriesDao;
        this.snippetDao = snippetDao;
    }

    @Override
    public Dao<String, Category> getCategoryDao() {
        return categoriesDao;
    }

    @Override
    public Dao<String, Snippet> getSnippetDao() {
        return snippetDao;
    }

    @Override
    public void handle(Context context) throws Exception {
           context.getResponse().send("foo");
    }
}
