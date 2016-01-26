/*
 * Copyright (c) 2016,  nwillc@gmail.com
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

package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.google.inject.Inject;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.github.nwillc.mysnipserver.util.rest.Params.KEY;

public class Categories extends SparkController<Category> {
    private final static Logger LOGGER = Logger.getLogger(Categories.class.getCanonicalName());

    @Inject
    public Categories(Dao<Category> dao) {
        super(dao);
        get("categories", this::findAll);
        post("categories", this::save);
        delete("categories/" + KEY.getLabel(), this::delete);
    }

    private List<Category> findAll(Request request, Response response) {
        return getDao().findAll().collect(Collectors.toList());
    }

    private Boolean delete(Request request, Response response) {
        LOGGER.info("Delete category: " + KEY.from(request));
        getDao().delete(KEY.from(request));
        return Boolean.TRUE;
    }

    private Boolean save(Request request, Response response) {
        try {
            final Category category = getMapper().get().readValue(request.body(), Category.class);
            LOGGER.info("Category: " + category);
            getDao().save(category);
            return Boolean.TRUE;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }
}
