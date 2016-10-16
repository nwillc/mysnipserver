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

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.nwillc.mysnipserver.controller.graphql.schema.SnippetSchema.*;

@GraphQLName(QUERY)
public final class QuerySchema extends DaoConsumer {

	@GraphQLField
	public static List<Category> categories(final DataFetchingEnvironment env) {
		return getCategoryDao(env).findAll().collect(Collectors.toList());
	}

	@GraphQLField
	public static Category category(final DataFetchingEnvironment env,
									@NotNull @GraphQLName(KEY) final String key) {
		return getCategoryDao(env).findOne(key).orElse(null);
	}

	@GraphQLField
	public static List<Snippet> snippets(final DataFetchingEnvironment env,
										 @GraphQLName(CATEGORY) final String category,
										 @GraphQLName(MATCH) final String match) {
		Stream<Snippet> snippetStream = match != null ? getSnippetDao(env).find(match) :
				getSnippetDao(env).findAll();
		if (category != null) {
			snippetStream = snippetStream.filter(snippet -> category.equals(snippet.getCategory()));
		}
		return snippetStream.collect(Collectors.toList());
	}

	@GraphQLField
	public static Snippet snippet(final DataFetchingEnvironment env,
								  @NotNull @GraphQLName(KEY) final String key) {
		return getSnippetDao(env).findOne(key).orElse(null);
	}
}
