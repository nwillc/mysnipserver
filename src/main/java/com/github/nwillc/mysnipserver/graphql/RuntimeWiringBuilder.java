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

package com.github.nwillc.mysnipserver.graphql;

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.graphql.fetchers.*;
import com.github.nwillc.opa.Dao;
import graphql.schema.idl.RuntimeWiring;

public class RuntimeWiringBuilder {
    public static RuntimeWiring getRuntimeWiring(Dao<String, Snippet> snippetDao, Dao<String, Category> categoryDao) {
        return RuntimeWiring.newRuntimeWiring()
                .type("QueryType", wiriing -> wiriing
                        .dataFetcher("snippet", new SnippetQuery(snippetDao))
                        .dataFetcher("snippets", new SnippetsQuery(snippetDao))
                        .dataFetcher("category", new CategoryQuery(categoryDao))
                        .dataFetcher("categories", new CategoriesQuery(categoryDao))
                        .dataFetcher("export", new ExportQuery(snippetDao, categoryDao))
                )
                .type("MutationType", wiring -> wiring
                        .dataFetcher("deleteSnippet", new SnippetDelete(snippetDao))
                        .dataFetcher("deleteCategory", new CategoryDelete(categoryDao))
                        .dataFetcher("category", new CategorySave(categoryDao))
                        .dataFetcher("snippet", new SnippetSave(snippetDao))
                )
                .build();
    }
}
