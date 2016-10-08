package com.github.nwillc.mysnipserver.controller.graphql.schema;

import graphql.schema.GraphQLSchema;

@FunctionalInterface
public interface SchemaProvider {
	public GraphQLSchema getSchema();
}
