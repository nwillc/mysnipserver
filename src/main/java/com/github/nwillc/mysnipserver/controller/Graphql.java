package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.controller.graphql.schema.HelloWorldSchema;
import com.github.nwillc.mysnipserver.controller.graphql.schema.SchemaProvider;
import com.github.nwillc.mysnipserver.util.ToJson;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.pmw.tinylog.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.nwillc.mysnipserver.util.rest.Version.versionedPath;

public class Graphql implements ToJson {
	public static final String GRAPHQL_PATH = "graphql";
	private final SchemaProvider schema = new HelloWorldSchema();
	private final GraphQL graphql = new GraphQL(schema.getSchema());

	public Graphql() {
		Spark.post(versionedPath(GRAPHQL_PATH), this::graphql, this::toJson );
	}

	private Map<String, Object> graphql(Request request, Response response) {
		ExecutionResult executionResult = graphql.execute("{hello}");
		Map<String, Object> result = new LinkedHashMap<>();
		if (executionResult.getErrors().size() > 0) {
			result.put("errors", executionResult.getErrors());
			Logger.error("Errors: {}", executionResult.getErrors());
		}
		result.put("data", executionResult.getData());
		return result;
	}
}
