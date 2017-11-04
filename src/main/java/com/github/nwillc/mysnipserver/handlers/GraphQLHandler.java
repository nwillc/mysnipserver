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
import com.github.nwillc.mysnipserver.graphql.schema.SnippetSchema;
import com.github.nwillc.opa.Dao;
import com.google.inject.Inject;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.pmw.tinylog.Logger;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.util.LinkedHashMap;
import java.util.Map;

import static ratpack.jackson.Jackson.json;

public class GraphQLHandler implements Handler, DaoProvider {
    public static final String PATH = "v1/graphql";
    private static final String QUERY = "query";
    private static final String ERRORS = "errors";
    private static final String DATA = "data";
    private final Dao<String, Category> categoriesDao;
    private final Dao<String, Snippet> snippetDao;
    private final GraphQL graphql;

    @Inject
    public GraphQLHandler(Dao<String, Category> categoriesDao,
                          Dao<String, Snippet> snippetDao) throws Exception {
        this.categoriesDao = categoriesDao;
        this.snippetDao = snippetDao;
        graphql = GraphQL.newGraphQL(new SnippetSchema().getSchema()).build();
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
        context.parse(Map.class).then(payload -> {
            Logger.info(QUERY + ": " + payload.get(QUERY));
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) payload.get("variables");
            ExecutionResult executionResult;
            try {
                executionResult = graphql.execute(payload.get(QUERY).toString(), null, this, variables);
            } catch (Throwable e) {
                Logger.warn("GraphQL failed", e);
                context.render("{  }");
                return;
            }

            Map<String, Object> result = new LinkedHashMap<>();
            if (executionResult.getErrors().isEmpty()) {
                result.put(DATA, executionResult.getData());
            } else {
                result.put(ERRORS, executionResult.getErrors());
                Logger.error("Errors: {}", executionResult.getErrors());
            }
            context.render(json(result));
        });
    }
}
