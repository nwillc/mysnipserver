/*
 * Copyright (c) 2015,  nwillc@gmail.com
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

package com.github.nwillc.mysnipserver;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.mysnipserver.controller.Categories;
import com.github.nwillc.mysnipserver.controller.Snippets;
import com.github.nwillc.mysnipserver.controller.SparkController;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.rest.error.HttpException;
import spark.Session;
import spark.servlet.SparkApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static spark.Spark.*;

class MySnipServerApplication implements SparkApplication {
    private final static Logger LOGGER = Logger.getLogger(MySnipServerApplication.class.getSimpleName());
    private final List<SparkController> controllers = new ArrayList<>(10);
    private final Dao<Category> categoriesDao;
    private final Dao<Snippet> snippetDao;

    public MySnipServerApplication(Dao<Category> categoriesDao, Dao<Snippet> snippetDao) {
        this.categoriesDao = categoriesDao;
        this.snippetDao = snippetDao;
    }

    @Override
    public void init() {
        LOGGER.info("Starting");
        // Static files
        staticFileLocation("/public");

        controllers.add(new Categories(categoriesDao));
        controllers.add(new Snippets(snippetDao));

        // Specific routes
        get("/ping", (request, response) -> "PONG");

        before((request, response) -> {
            Session session = request.session(true);
            if (Boolean.TRUE != session.<Boolean>attribute("login.isDone")) {
                session.attribute("login.isDone", Boolean.TRUE);
                response.redirect("/login.html");
            }
        });

        exception(HttpException.class, (e, request, response) -> {
            response.status(((HttpException)e).getCode().code);
            response.body(((HttpException)e).getCode().toString());
            LOGGER.info("Returning: " + e.toString());
        });

        LOGGER.info("Completed");
    }
}
