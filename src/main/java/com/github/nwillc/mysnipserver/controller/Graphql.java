package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.util.ToJson;
import spark.Request;
import spark.Response;
import spark.Spark;

import static com.github.nwillc.mysnipserver.util.rest.Version.versionedPath;

public class Graphql implements ToJson {
	public static final String GRAPHQL_PATH = "graphql";

	public Graphql() {
		Spark.post(versionedPath(GRAPHQL_PATH), this::graphql, this::toJson );
	}

	private Object graphql(Request request, Response response) {
		return "hello world";
	}
}
