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

package com.github.nwillc.mysnipserver;

import com.github.nwillc.mysnipserver.controller.Authentication;
import com.github.nwillc.mysnipserver.controller.Categories;
import com.github.nwillc.mysnipserver.controller.Snippets;
import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.dao.memory.CategoryDao;
import com.github.nwillc.mysnipserver.dao.memory.SnippetDao;
import com.github.nwillc.mysnipserver.dao.memory.UserDao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.mysnipserver.util.http.error.HttpException;
import com.github.nwillc.mysnipserver.util.rest.Version;
import org.pmw.tinylog.Logger;
import spark.servlet.SparkApplication;

import static com.github.nwillc.mysnipserver.util.rest.Version.versionedPath;
import static spark.Spark.*;

public class MySnipServerApplication implements SparkApplication {
    private final Dao<Category> categoriesDao = new CategoryDao();
    private final Dao<Snippet> snippetDao = new SnippetDao(categoriesDao);
    private final Dao<User> userDao = new UserDao();
    private String properties = "";

    @Override
    public void init() {
        Logger.info("Starting");
        // Static files
        // staticFileLocation("/public");

        // Create controllers
        new Categories(categoriesDao);
        new Snippets(snippetDao, categoriesDao);
        new Authentication(userDao);

        // Specific routes
        get(versionedPath("ping"), (request, response) -> "PONG");
        get(versionedPath("properties"), (request, response) -> properties);

        exception(HttpException.class, (e, request, response) -> {
            response.status(((HttpException) e).getCode().code);
            response.body(((HttpException) e).getCode().toString());
            Logger.info("Returning: " + e.toString());
        });

        Logger.info("Completed");
    }
}
