package com.github.nwillc.mysnipserver.controller.graphql.schema;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;

import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

public class SnippetSchema implements SchemaProvider {
	private final GraphQLSchema schema;
	private final GraphQLInterfaceType entity = newInterface()
			.name("entity")
			.description("An entity")
			.field(newFieldDefinition()
					.name("key")
					.description("Entity identifier")
					.type(GraphQLString)
					.build())
			.typeResolver(new SnippetTypeResolver())
			.build();
	private final GraphQLObjectType category = newObject()
			.name("category")
			.description("Category of a snippet")
			.withInterface(entity)
			.field(newFieldDefinition()
					.name("key")
					.description("Entity identifier")
					.type(GraphQLString)
					.build())
			.field(newFieldDefinition()
					.name("name")
					.description("Category Name")
					.type(GraphQLString)
					.build())
			.build();
	private final GraphQLObjectType snippet = newObject()
			.name("snippet")
			.description("A snippet")
			.withInterface(entity)
			.field(newFieldDefinition()
					.name("key")
					.description("Entity identifier")
					.type(GraphQLString)
					.build())
			.field(newFieldDefinition()
					.name("category")
					.description("Category identifier")
					.type(GraphQLString)
					.build())
			.field(newFieldDefinition()
					.name("title")
					.description("Snippet title")
					.type(GraphQLString)
					.build())
			.field(newFieldDefinition()
					.name("body")
					.description("Snippet body")
					.type(GraphQLString)
					.build())
			.build();

	public SnippetSchema(Dao<Category> categoryDao, Dao<Snippet> snippetDao) {
		GraphQLObjectType query = newObject()
				.name("query")
				.field(newFieldDefinition()
						.name("category")
						.type(category)
						.argument(newArgument()
								.name("key")
								.description("The category id")
								.type(new GraphQLNonNull(GraphQLString))
								.build())
						.dataFetcher(environment -> categoryDao.findOne(environment.getArgument("key")).orElse(null))
						.build())
				.field(newFieldDefinition()
						.name("categories")
						.type(new GraphQLList(category))
						.dataFetcher(environment -> categoryDao.findAll().collect(Collectors.toList()))
						.build())
				.field(newFieldDefinition()
						.name("snippet")
						.type(snippet)
						.argument(newArgument()
								.name("key")
								.description("The category id")
								.type(new GraphQLNonNull(GraphQLString))
								.build())
						.dataFetcher(environment -> snippetDao.findOne(environment.getArgument("key")).orElse(null))
						.build())
				.field(newFieldDefinition()
						.name("snippets")
						.type(new GraphQLList(snippet))
						.dataFetcher(environment -> snippetDao.findAll().collect(Collectors.toList()))
						.build())
				.build();
		schema = newSchema()
				.query(query)
				.build();
	}

	@Override
	public GraphQLSchema getSchema() {
		return schema;
	}

	private class SnippetTypeResolver implements TypeResolver {
		@Override
		public GraphQLObjectType getType(Object object) {
			if (object instanceof Category) {
				return category;
			} else if (object instanceof Snippet) {
				return snippet;
			} else {
				return null;
			}
		}
	}
}
