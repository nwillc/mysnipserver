package com.github.nwillc.mysnipserver.dao.mongodb;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Entity;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MongoDbDao<T extends Entity> implements Dao<T> {
    @Override
    public Optional<T> findOne(String key) {
        return null;
    }

    @Override
    public Stream<T> findAll() {
        return null;
    }

    @Override
    public Stream<T> find(Predicate<T> predicate) {
        return null;
    }

    @Override
    public void save(T entity) {

    }

    @Override
    public void delete(String key) {

    }
}
