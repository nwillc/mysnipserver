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
import graphql.annotations.GraphQLAnnotations;
import graphql.schema.*;

import java.util.Optional;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

public class SnippetSchema implements SchemaProvider {
    public static final String CATEGORY = "category";
    public static final String KEY = "key";
    public static final String SNIPPET = "snippet";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String MATCH = "match";
    public static final String QUERY = "query";
    private final GraphQLSchema schema;


    public SnippetSchema(Dao<Category> categoryDao, Dao<Snippet> snippetDao) throws IllegalAccessException, NoSuchMethodException, InstantiationException {
        QuerySchema.setCategoryDao(categoryDao);
        QuerySchema.setSnippetDao(snippetDao);
        schema = newSchema()
                .query(GraphQLAnnotations.object(QuerySchema.class))
                //	.mutation(mutations(categoryDao, snippetDao))
                .build();
    }

    private GraphQLObjectType mutations(Dao<Category> categoryDao, Dao<Snippet> snippetDao) {
        return newObject()
                .name("mutation")
                .field(newFieldDefinition()
                        .name(CATEGORY)
                        .type(new GraphQLTypeReference(CATEGORY))
                        .argument(newArgument()
                                .name(KEY)
                                .description("The category id")
                                .type(GraphQLString)
                                .build())
                        .argument(newArgument()
                                .name(NAME)
                                .type(new GraphQLNonNull(GraphQLString))
                                .build())
                        .dataFetcher(categoryMutation(categoryDao))
                        .build())
                .field(newFieldDefinition()
                        .name("deleteCategory")
                        .type(GraphQLBoolean)
                        .argument(newArgument()
                                .name(KEY)
                                .description("The category id")
                                .type(new GraphQLNonNull(GraphQLString))
                                .build())
                        .dataFetcher(environment -> {
                            categoryDao.delete(getArgument(environment, KEY).orElse(null));
                            return true;
                        })
                        .build())
                .field(newFieldDefinition()
                        .name(SNIPPET)
                        .type(new GraphQLTypeReference(SNIPPET))
                        .argument(newArgument()
                                .name(KEY)
                                .description("The snippet id")
                                .type(GraphQLString)
                                .build())
                        .argument(newArgument()
                                .name(CATEGORY)
                                .type(new GraphQLNonNull(GraphQLString))
                                .build())
                        .argument(newArgument()
                                .name(TITLE)
                                .type(new GraphQLNonNull(GraphQLString))
                                .build())
                        .argument(newArgument()
                                .name(BODY)
                                .type(new GraphQLNonNull(GraphQLString))
                                .build())
                        .dataFetcher(snippetMutation(categoryDao, snippetDao))
                        .build())
                .field(newFieldDefinition()
                        .name("deleteSnippet")
                        .type(GraphQLBoolean)
                        .argument(newArgument()
                                .name(KEY)
                                .description("The snippet id")
                                .type(new GraphQLNonNull(GraphQLString))
                                .build())
                        .dataFetcher(environment -> {
                            snippetDao.delete(getArgument(environment, KEY).orElse(null));
                            return true;
                        })
                        .build())
                .build();
    }

    @Override
    public GraphQLSchema getSchema() {
        return schema;
    }

    private static DataFetcher snippetMutation(Dao<Category> categoryDao, Dao<Snippet> snippetDao) {
        return environment -> {
            Optional<String> key = getArgument(environment, KEY);
            Optional<String> category = getArgument(environment, CATEGORY);
            Optional<String> title = getArgument(environment, TITLE);
            Optional<String> body = getArgument(environment, BODY);

            if (!categoryDao.findOne(category.get()).isPresent()) {
                return null;
            }

            final Snippet snippet = new Snippet(category.get(), title.get(), body.get());
            key.ifPresent(snippet::setKey);
            snippetDao.save(snippet);
            return snippet;
        };
    }

    private static DataFetcher categoryMutation(Dao<Category> categoryDao) {
        return environment -> {
            Optional<String> name = getArgument(environment, NAME);
            Optional<String> key = getArgument(environment, KEY);

            final Category category = new Category(name.get());

            key.ifPresent(category::setKey);
            categoryDao.save(category);
            return category;
        };
    }

    public static Optional<String> getArgument(final DataFetchingEnvironment environment, final String arg) {
        return Optional.ofNullable(environment.getArgument(arg));
    }

}
