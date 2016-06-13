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

import com.github.nwillc.mysnipserver.controller.model.Query;
import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.mysnipserver.util.http.error.HttpException;
import com.google.inject.Inject;
import org.pmw.tinylog.Logger;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.nwillc.mysnipserver.util.rest.Params.CATEGORY;
import static com.github.nwillc.mysnipserver.util.rest.Params.KEY;

public class Snippets extends SparkController<Snippet> {
    private final Dao<Category> categoryDao;

    @Inject
    public Snippets(Dao<Snippet> dao, Dao<Category> categoryDao) {
        super(dao);
        this.categoryDao = categoryDao;
        get("snippets", this::findAll);
        get("snippets/category/" + KEY.getLabel(), this::find);
        get("snippets/" + KEY.getLabel(), this::findOne);
        post("snippets/category/" + KEY.getLabel(), this::searchCategory);
        post("snippets", this::save);
        put("snippets/" + KEY.getLabel() + "/move/" + CATEGORY.getLabel(), this::move);
        delete("snippets/" + KEY.getLabel(), this::delete);
    }

    private Boolean move(Request request, Response response) {
        try {
            final String snippetKey = KEY.from(request);
            final String categoryKey = CATEGORY.from(request);
            Logger.info("Moving " + snippetKey + " to " + categoryKey);
            final Optional<Snippet> snippet = getDao().findOne(snippetKey);
            final Optional<Category> category = categoryDao.findOne(categoryKey);
            snippet.ifPresent(s -> category.ifPresent(c -> {
                s.setCategory(c.getKey());
                Logger.info("Was: " + snippet.get() + "\nNow: " + s);
                getDao().save(s);
            }));
        } catch (Exception e) {
            throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Move failed");
        }

        return Boolean.TRUE;
    }

    private List<Snippet> findAll(Request request, Response response) {
        Logger.info("Requesting all");
        return getDao().findAll().collect(Collectors.toList());
    }

    private List<Snippet> find(Request request, Response response) {
        Logger.info("Finding snippets in category: " + KEY.from(request));
        return getDao().findAll().filter(snippet -> KEY.from(request).equals(snippet.getCategory())).collect(Collectors.toList());
    }

    private List<Snippet> searchCategory(Request request, Response response) {
        try {
            final Query query = getMapper().get().readValue(request.body(), Query.class);
            Logger.info("Searching category: " + KEY.from(request) + " with query: " + query.getQuery());
            return getDao().find(query.getQuery()).filter(snippet -> KEY.from(request).equals(snippet.getCategory())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Search failed");
        }

    }

    private Snippet findOne(Request request, Response response) {
        Snippet snippet = getDao().findOne(KEY.from(request)).orElseThrow(() -> new HttpException(HttpStatusCode.NOT_FOUND));
        Logger.info("Returning: " + snippet);
        return snippet;
    }

    private Boolean delete(Request request, Response response) {
        Logger.info("Delete snippet: " + KEY.from(request));
        getDao().delete(KEY.from(request));
        return Boolean.TRUE;
    }

    private Boolean save(Request request, Response response) {
        try {
            final Snippet snippet = getMapper().get().readValue(request.body(), Snippet.class);
            Logger.info("Saving snippet: " + snippet);
            getDao().save(snippet);
            Logger.info("We think its: " + getDao().findOne(snippet.getKey()));
            return Boolean.TRUE;
        } catch (Exception e) {
            throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Failed saving snippet");
        }
    }
}
