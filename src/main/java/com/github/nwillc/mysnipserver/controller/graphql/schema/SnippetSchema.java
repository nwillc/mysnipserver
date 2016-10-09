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

package com.github.nwillc.mysnipserver.controller.graphql.schema;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

public class SnippetSchema implements SchemaProvider {
	private final GraphQLSchema schema;
	private final GraphQLObjectType category = newObject()
			.name("category")
			.description("Category of a snippet")
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

}
