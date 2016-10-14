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
import graphql.schema.GraphQLSchema;

import static graphql.schema.GraphQLSchema.newSchema;

public class SnippetSchema {
    public static final String CATEGORY = "category";
    public static final String KEY = "key";
    public static final String SNIPPET = "snippet";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String MATCH = "match";
    public static final String QUERY = "query";
    public static final String MUTATION = "mutation";
    private final GraphQLSchema schema;


    public SnippetSchema(Dao<Category> categoryDao, Dao<Snippet> snippetDao) throws IllegalAccessException, NoSuchMethodException, InstantiationException {
        QuerySchema.setCategoryDao(categoryDao);
        QuerySchema.setSnippetDao(snippetDao);
        MutationSchema.setCategoryDao(categoryDao);
        MutationSchema.setSnippetDao(snippetDao);
        schema = newSchema()
                .query(GraphQLAnnotations.object(QuerySchema.class))
                .mutation(GraphQLAnnotations.object(MutationSchema.class))
                .build();
    }

    public GraphQLSchema getSchema() {
        return schema;
    }

}
