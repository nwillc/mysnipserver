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
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;

import javax.validation.constraints.NotNull;

import static com.github.nwillc.mysnipserver.controller.graphql.schema.SnippetSchema.*;

@GraphQLName(MUTATION)
public final class MutationSchema {
    private static Dao<Category> categoryDao;
    private static Dao<Snippet> snippetDao;

    public static void setCategoryDao(Dao<Category> categoryDao) {
        MutationSchema.categoryDao = categoryDao;
    }

    public static void setSnippetDao(Dao<Snippet> snippetDao) {
        MutationSchema.snippetDao = snippetDao;
    }

    @GraphQLField
    public static Category category(@GraphQLName(KEY) final String key,
                                    @NotNull @GraphQLName(NAME) final String name) {
        final Category category = new Category(name);
        if (key != null) {
            category.setKey(key);
        }
        categoryDao.save(category);
        return category;
    }

    @GraphQLField
    public static Snippet snippet(@GraphQLName(KEY) final String key,
                                  @NotNull @GraphQLName(CATEGORY) final String category,
                                  @NotNull @GraphQLName(TITLE) final String title,
                                  @NotNull @GraphQLName(BODY) final String body) {
        final Snippet snippet = new Snippet(category, title, body);
        if (key != null) {
            snippet.setKey(key);
        }
        snippetDao.save(snippet);
        return snippet;
    }

    @GraphQLField
    public static boolean deleteCategory(@NotNull @GraphQLName(KEY) final String key) {
        categoryDao.delete(key);
        return true;
    }

    @GraphQLField
    public static boolean deleteSnippet(@NotNull @GraphQLName(KEY) final String key) {
        snippetDao.delete(key);
        return true;
    }
}
