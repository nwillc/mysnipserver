package com.github.nwillc.mysnipserver.controller.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;


import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public class HelloWorldSchema implements SchemaProvider {
	private final GraphQLObjectType queryType = newObject()
			.name("helloWorldQuery")
			.field(newFieldDefinition()
					.type(GraphQLString)
					.name("hello")
					.staticValue("world").build())
			.build();
	private final GraphQLSchema schema = GraphQLSchema.newSchema()
			.query(queryType)
			.build();

	@Override
	public GraphQLSchema getSchema() {
		return schema;
	}
}
