package com.github.nwillc.mysnipserver.controller.graphql.schema;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.TypeResolver;
import org.pmw.tinylog.Logger;

import java.util.UUID;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

public class SnippetSchema implements SchemaProvider {
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
	public final GraphQLObjectType category = newObject()
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
	private final GraphQLObjectType query;
	private final GraphQLSchema schema;
	private final Dao<Category> categoryDao;

	public SnippetSchema(Dao<Category> categoryDao) {
		this.categoryDao = categoryDao;
		query = newObject()
				.name("query")
				.field(newFieldDefinition()
						.name("category")
						.type(category)
						.argument(newArgument()
								.name("key")
								.description("The category id")
								.type(new GraphQLNonNull(GraphQLString))
								.build())
						.dataFetcher(new CategoryFetcher())
						.build())
					.field(newFieldDefinition()
							.name("categories")
							.type(new GraphQLList(new GraphQLTypeReference("category")))
							.dataFetcher(new CategoriesFetcher())
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

	private class CategoryFetcher implements DataFetcher {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			if (environment.containsArgument("key")) {
				String key = environment.getArgument("key");
				Logger.info("Getting category: " + key);
				return categoryDao.findOne(key).orElse(null);
			}
			return null;
		}
	}

	private class CategoriesFetcher implements DataFetcher {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			return categoryDao.findAll().collect(Collectors.toList());
		}
	}

	private class SnippetTypeResolver implements TypeResolver {
		@Override
		public GraphQLObjectType getType(Object object) {
			return category;
		}
	}
}
