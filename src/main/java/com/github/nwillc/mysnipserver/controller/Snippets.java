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
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import com.github.nwillc.mysnipserver.util.http.error.HttpException;
import com.google.inject.Inject;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.github.nwillc.mysnipserver.util.rest.Params.KEY;

public class Snippets extends SparkController<Snippet> {
    private final static Logger LOGGER = Logger.getLogger(Snippets.class.getCanonicalName());

    @Inject
    public Snippets(Dao<Snippet> dao) {
        super(dao);
        get("snippets", this::findAll);
        get("snippets/category/" + KEY.getLabel(), this::find);
        get("snippets/" + KEY.getLabel(), this::findOne);
        post("snippets/category/" + KEY.getLabel(), this::searchCategory);
        post("snippets", this::save);
        delete("snippets/" + KEY.getLabel(), this::delete);
    }

    public List<Snippet> findAll(Request request, Response response) {
        LOGGER.info("Requesting all");
        return getDao().findAll().collect(Collectors.toList());
    }

    public List<Snippet> find(Request request, Response response) {
        LOGGER.info("Finding snippets in category: " + KEY.from(request));
        return getDao().findAll().filter(snippet -> KEY.from(request).equals(snippet.getCategory())).collect(Collectors.toList());
    }

    public List<Snippet> searchCategory(Request request, Response response) {
        try {
            final Query query = getMapper().get().readValue(request.body(), Query.class);
            LOGGER.info("Searching category: " + KEY.from(request) + " with query: " + query.getQuery());
            return getDao().find(query.getQuery()).filter(snippet -> KEY.from(request).equals(snippet.getCategory())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Search failed");
        }

    }

    public Snippet findOne(Request request, Response response) {
        return getDao().findOne(KEY.from(request)).orElseThrow(() -> new HttpException(HttpStatusCode.NOT_FOUND));
    }

    public Boolean delete(Request request, Response response) {
        LOGGER.info("Delete snippet: " + KEY.from(request));
        getDao().delete(KEY.from(request));
        return Boolean.TRUE;
    }

    public Boolean save(Request request, Response response) {
        try {
            final Snippet snippet = getMapper().get().readValue(request.body(), Snippet.class);
            LOGGER.info("Saving snippet: " + snippet);
            getDao().save(snippet);
            return Boolean.TRUE;
        } catch (Exception e) {
            throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Failed saving snippet");
        }
    }
}
