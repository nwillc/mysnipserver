package com.github.nwillc.mysnipserver.util.guice;

import com.github.nwillc.mysnipserver.MySnipServerApplication;
import com.github.nwillc.mysnipserver.dao.mongodb.MongoDbDao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.entity.User;
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
        MongoDbDao<User> userDao = new MongoDbDao<>(client, User.class);
        User user = new User("foo", "nwillc2");
        userDao.save(user);
        Logger.info("Find: " + userDao.findOne(user.getKey()).orElse(null));
        bind(new TypeLiteral<MySnipServerApplication>() {
        }).toInstance(new MySnipServerApplication(
                new MongoDbDao<>(client, Category.class),
                new MongoDbDao<>(client, Snippet.class),
                userDao));
    }
}
