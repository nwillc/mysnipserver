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

package com.github.nwillc.mysnipserver.util.guice;

import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.opa.CachingDao;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.mongo.MongoDbDao;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.pmw.tinylog.Logger;

import java.util.Collections;
import java.util.List;

public class MongoDbModule implements Module {
    private static final String MONGO_DB_SERVER = System.getenv("MONGO_DB_SERVER");
    private static final String MONGO_DB_PORT = System.getenv("MONGO_DB_PORT");
    private static final String MONGO_DB_USER = System.getenv("MONGO_DB_USER");
    private static final String MONGO_DB_PASSWORD = System.getenv("MONGO_DB_PASSWORD");
    private static final String DATABASE = "snippets";

    @Override
    public void configure(Binder binder) {
        Logger.info("Configuring MongoDb Module");
        ServerAddress serverAddress = new ServerAddress(MONGO_DB_SERVER, Integer.parseInt(MONGO_DB_PORT));
        MongoCredential credential = MongoCredential.createCredential(MONGO_DB_USER,
                DATABASE,
                MONGO_DB_PASSWORD.toCharArray());
        List<MongoCredential> auths = Collections.singletonList(credential);
        MongoClient client = new MongoClient(serverAddress, auths);
        Dao<String, Category> categoryDao = new MongoDbDao<>(client, DATABASE, Category.class);
        Dao<String, Snippet> snippetDao = new CachingDao<>(new MongoDbDao<>(client, DATABASE, Snippet.class));
        Dao<String, User> userDao = new MongoDbDao<>(client, DATABASE, User.class);
        binder.bind(new TypeLiteral<Dao<String, Category>>(){}).toInstance(categoryDao);
        binder.bind(new TypeLiteral<Dao<String, Snippet>>(){}).toInstance(snippetDao);
        binder.bind(new TypeLiteral<Dao<String, User>>(){}).toInstance(userDao);
    }
}
