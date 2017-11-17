/*
 * Copyright 2017 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.mysnipserver.graphql.fetchers;

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.opa.Dao;
import graphql.schema.DataFetchingEnvironment;

import java.util.Optional;

public class CategorySave extends DaoFetcher<String, Category, Category> {
    public CategorySave(Dao<String, Category> dao) {
        super(dao);
    }

    @Override
    public Category get(DataFetchingEnvironment environment) {
        final String name = environment.getArgument("name");
        final Category category = new Category(name);
        if (environment.containsArgument("key")) {
            category.setKey(environment.getArgument("key"));
        }
        getDao().save(category);
        return category;
    }
}