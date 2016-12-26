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

package com.github.nwillc.mysnipserver.util.guice;

import com.github.nwillc.mysnipserver.MySnipServerApplication;
import com.github.nwillc.mysnipserver.dao.mongodb.MongoDbDao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.opa.CachingDao;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.pmw.tinylog.Logger;

import java.util.Collections;
import java.util.List;

public class MongoDbModule extends AbstractModule {
    private static final String MONGO_DB_SERVER = System.getenv("MONGO_DB_SERVER");
    private static final String MONGO_DB_PORT = System.getenv("MONGO_DB_PORT");
    private static final String MONGO_DB_USER = System.getenv("MONGO_DB_USER");
    private static final String MONGO_DB_PASSWORD = System.getenv("MONGO_DB_PASSWORD");

    @Override
    protected void configure() {
        Logger.info("Configuring MongoDb Module");
        ServerAddress serverAddress = new ServerAddress(MONGO_DB_SERVER, Integer.parseInt(MONGO_DB_PORT));
        MongoCredential credential = MongoCredential.createCredential(MONGO_DB_USER,
                "snippets",
                MONGO_DB_PASSWORD.toCharArray());
        List<MongoCredential> auths = Collections.singletonList(credential);
        MongoClient client = new MongoClient(serverAddress, auths);
        MongoDbDao<String, User> userDao = new MongoDbDao<>(client, User.class);
        User user = new User("foo", "nwillc");
        userDao.save(user);
        Logger.info("Find: " + userDao.findOne(user.getKey()).orElse(null));
        userDao.delete(user.getKey());
        Logger.info("Find: " + userDao.findOne(user.getKey()).orElse(null));
        bind(new TypeLiteral<MySnipServerApplication>() {
        }).toInstance(new MySnipServerApplication(
                new MongoDbDao<>(client, Category.class),
                new CachingDao<>(new MongoDbDao<>(client, Snippet.class)),
                userDao));
    }
}
