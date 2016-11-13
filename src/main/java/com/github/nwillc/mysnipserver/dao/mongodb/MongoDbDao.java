package com.github.nwillc.mysnipserver.dao.mongodb;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Entity;
import com.github.nwillc.mysnipserver.util.ToJson;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MongoDbDao<T extends Entity> implements Dao<T>, ToJson {
    private final MongoClient client;
    private final Class<T> tClass;
    private final MongoCollection collection;

    public MongoDbDao(final MongoClient client, final Class<T> tClass) {
        this.client = client;
        this.tClass = tClass;
        collection =  client.getDatabase("snippets").getCollection(tClass.getSimpleName());
    }

    @Override
    public Optional<T> findOne(String key) {
        return null;
    }

    @Override
    public Stream<T> findAll() {
        FindIterable<T> findIterable = collection.find();
        return StreamSupport.stream(findIterable.spliterator(),false);
    }

    @Override
    public Stream<T> find(Predicate<T> predicate) {
        return null;
    }

    @Override
    public void save(T entity) {
        collection.insertOne(entity);
    }

    @Override
    public void delete(String key) {

    }
}
