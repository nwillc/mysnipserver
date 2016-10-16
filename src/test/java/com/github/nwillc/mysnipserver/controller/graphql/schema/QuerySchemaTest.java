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

import graphql.annotations.GraphQLAnnotations;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.junit.Before;
import org.junit.Test;

import static com.github.nwillc.mysnipserver.controller.graphql.schema.SnippetSchema.*;
import static org.assertj.core.api.Assertions.assertThat;


public class QuerySchemaTest {
	private GraphQLObjectType querySchema;

	@Before
	public void setUp() throws Exception {
		querySchema = GraphQLAnnotations.object(QuerySchema.class);
	}

	@Test
	public void testCount() throws Exception {
		assertThat(querySchema.getFieldDefinitions()).hasSize(4);
	}

	@Test
	public void testCategory() throws Exception {
		final GraphQLFieldDefinition category = querySchema.getFieldDefinition(CATEGORY);
		assertThat(category).isNotNull();
		assertThat(category.getArguments()).hasSize(1);
		assertThat(category.getArgument(KEY)).isNotNull();
		assertThat(category.getDataFetcher()).isNotNull();
		assertThat(category.getType()).isNotNull();
	}

	@Test
	public void testCategories() throws Exception {
		final GraphQLFieldDefinition categories = querySchema.getFieldDefinition("categories");
		assertThat(categories).isNotNull();
		assertThat(categories.getArguments()).hasSize(0);
		assertThat(categories.getDataFetcher()).isNotNull();
		assertThat(categories.getType()).isNotNull();
	}

	@Test
	public void testSnippet() throws Exception {
		final GraphQLFieldDefinition snippet = querySchema.getFieldDefinition(SNIPPET);
		assertThat(snippet).isNotNull();
		assertThat(snippet.getArguments()).hasSize(1);
		assertThat(snippet.getArgument(KEY)).isNotNull();
		assertThat(snippet.getDataFetcher()).isNotNull();
		assertThat(snippet.getType()).isNotNull();
	}

	@Test
	public void testSnippets() throws Exception {
		final GraphQLFieldDefinition snippets = querySchema.getFieldDefinition("snippets");
		assertThat(snippets).isNotNull();
		assertThat(snippets.getArguments()).hasSize(2);
		assertThat(snippets.getArgument(CATEGORY));
		assertThat(snippets.getArgument(MATCH));
		assertThat(snippets.getDataFetcher()).isNotNull();
		assertThat(snippets.getType()).isNotNull();
	}

}