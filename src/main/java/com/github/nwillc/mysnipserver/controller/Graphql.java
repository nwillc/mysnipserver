/*
 * Copyright (c) 2016, nwillc@gmail.com
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
 *
 */

package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.controller.graphql.schema.SchemaProvider;
import com.github.nwillc.mysnipserver.controller.graphql.schema.SnippetSchema;
import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.util.ToJson;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.mysnipserver.util.http.error.HttpException;
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

public class Graphql implements ToJson {
	public static final String GRAPHQL_PATH = "graphql";
	private static final String QUERY = "query";
	private static final String ERRORS = "errors";
	private static final String DATA = "data";
	private final GraphQL graphql;

	public Graphql(Dao<Category> categoryDao, Dao<Snippet> snippetDao) {
		SchemaProvider schema = new SnippetSchema(categoryDao, snippetDao);
		graphql = new GraphQL(schema.getSchema());
		Spark.post(versionedPath(GRAPHQL_PATH), this::graphql, this::toJson);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> graphql(Request request, Response response) {
		Map<String, String> payload;
		try {
			payload = getMapper().readValue(request.body(), Map.class);
		} catch (IOException e) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST, "Could not parse request body as GraphQL map.");
		}
		Logger.info(QUERY + ": " + payload.get(QUERY));
		ExecutionResult executionResult = graphql.execute(payload.get(QUERY));
		Map<String, Object> result = new LinkedHashMap<>();
		if (executionResult.getErrors().size() > 0) {
			result.put(ERRORS, executionResult.getErrors());
			Logger.error("Errors: {}", executionResult.getErrors());
		}
		result.put(DATA, executionResult.getData());
		return result;
	}
}
