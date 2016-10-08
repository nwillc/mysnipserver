package com.github.nwillc.mysnipserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nwillc.mysnipserver.controller.graphql.schema.HelloWorldSchema;
import com.github.nwillc.mysnipserver.controller.graphql.schema.SchemaProvider;
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
	private final SchemaProvider schema = new HelloWorldSchema();
	private final GraphQL graphql = new GraphQL(schema.getSchema());

	public Graphql() {
		Spark.post(versionedPath(GRAPHQL_PATH), this::graphql, this::toJson );
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> graphql(Request request, Response response) {
		Map<String,String> payload;
		try {
			payload = getMapper().readValue(request.body(), Map.class);
		} catch (IOException e) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST, "Could not parse request body as GraphQL map.");
		}
		Logger.info(payload.get(QUERY));
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
