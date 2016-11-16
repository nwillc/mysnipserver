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

package com.github.nwillc.mysnipserver.dao;

import com.github.nwillc.mysnipserver.entity.Entity;
import com.github.nwillc.mysnipserver.util.CacheFactory;

import javax.cache.Cache;
import javax.cache.configuration.Factory;
import javax.cache.integration.CacheLoader;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CachingDao<T extends Entity> implements Dao<T> {
    private final Dao<T> dao;
    private final Class<T> tClass;
    private final Cache<String, T> cache;

    public CachingDao(final Dao<T> dao, final Class<T> tClass,
                      final Factory<CacheLoader<String, T>> factory) {
        this.dao = dao;
        this.tClass = tClass;
        cache = CacheFactory.getCache(tClass, factory);
    }

    @Override
    public Optional<T> findOne(String key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public Stream<T> findAll() {
        return dao.findAll().peek(entity -> cache.put(entity.getKey(), entity));
    }

    @Override
    public Stream<T> find(Predicate<T> predicate) {
        return dao.find(predicate).peek(entity -> cache.put(entity.getKey(), entity));
    }

    @Override
    public void save(T entity) {
        dao.save(entity);
        cache.put(entity.getKey(), entity);
    }

    @Override
    public void delete(String key) {
        dao.delete(key);
        cache.remove(key);
    }

    private T get(String key) {
        return cache.get(key);
    }
}
