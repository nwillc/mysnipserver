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

package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.DaoProvider;
import com.github.nwillc.mysnipserver.controller.graphql.schema.SnippetSchema;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.util.JsonMapper;
import com.github.nwillc.mysnipserver.util.http.HttpException;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.opa.Dao;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.pmw.tinylog.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.nwillc.mysnipserver.util.rest.Version.versionedPath;

public class Graphql implements JsonMapper, DaoProvider {
    static final String GRAPHQL_PATH = "graphql";
    private static final String QUERY = "query";
    private static final String ERRORS = "errors";
    private static final String DATA = "data";
    private final GraphQL graphql;
    private final Dao<String, Category> categoryDao;
    private final Dao<String, Snippet> snippetDao;

    public Graphql(Dao<String, Category> categoryDao,
                   Dao<String, Snippet> snippetDao) throws IllegalAccessException, NoSuchMethodException, InstantiationException {
        this.categoryDao = categoryDao;
        this.snippetDao = snippetDao;
        graphql = new GraphQL(new SnippetSchema().getSchema());
        Spark.post(versionedPath(GRAPHQL_PATH), this::graphql, this::toJson);
    }

    @Override
    public Dao<String, Category> getCategoryDao() {
        return categoryDao;
    }

    @Override
    public Dao<String, Snippet> getSnippetDao() {
        return snippetDao;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> graphql(Request request, Response response) {
        Map<String, Object> payload;
        try {
            payload = getMapper().readValue(request.body(), Map.class);
        } catch (IOException e) {
            throw new HttpException(HttpStatusCode.BAD_REQUEST, "Could not parse request body as GraphQL map.");
        }
        Map<String, Object> variables = (Map<String, Object>) payload.get("variables");
        Logger.info(QUERY + ": " + payload.get(QUERY));
        ExecutionResult executionResult = graphql.execute(payload.get(QUERY).toString(), null, this, variables);
        Map<String, Object> result = new LinkedHashMap<>();
        if (!executionResult.getErrors().isEmpty()) {
            result.put(ERRORS, executionResult.getErrors());
            Logger.error("Errors: {}", executionResult.getErrors());
        }
        result.put(DATA, executionResult.getData());
        response.type("application/json");
        return result;
    }
}
