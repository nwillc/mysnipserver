/*
 * Copyright (c) 2017, nwillc@gmail.com
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
 */

package com.github.nwillc.mysnipserver.graphql.schema;

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import graphql.schema.DataFetchingEnvironment;

import javax.validation.constraints.NotNull;

public final class MutationSchema extends DaoConsumer {

    public static Category category(final DataFetchingEnvironment env,
                                    final String key,
                                    final String name) {
        final Category category = new Category(name);
        if (key != null) {
            category.setKey(key);
        }
        getCategoryDao(env).save(category);
        return category;
    }

    public static Snippet snippet(final DataFetchingEnvironment env,
                                  final String key,
                                  @NotNull final String category,
                                  @NotNull final String title,
                                  @NotNull final String body) {
        final Snippet snippet = new Snippet(category, title, body);
        if (key != null) {
            snippet.setKey(key);
        }
        getSnippetDao(env).save(snippet);
        return snippet;
    }

    public static boolean deleteCategory(final DataFetchingEnvironment env,
                                         @NotNull final String key) {
        getCategoryDao(env).delete(key);
        return true;
    }

    public static boolean deleteSnippet(final DataFetchingEnvironment env,
                                        @NotNull final String key) {
        getSnippetDao(env).delete(key);
        return true;
    }
}
