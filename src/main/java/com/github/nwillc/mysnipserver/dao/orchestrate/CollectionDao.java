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

package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.dao.Entity;
import io.orchestrate.client.Client;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.Result;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

@CacheDefaults(cacheResolverFactory = ResolverFactory.class)
public class CollectionDao<T extends Entity> implements Dao<T> {
    private static final Logger LOGGER = Logger.getLogger(CollectionDao.class.getName());
    private static final int LIMIT = 100;
    private final Class<T> tClass;
    private final String collection;
    private final Client client;

    public CollectionDao(Client client, Class<T> tClass) {
        this(client, tClass.getSimpleName(), tClass);
    }

    public CollectionDao(Client client, String collection, Class<T> tClass) {
        this.collection = collection;
        this.tClass = tClass;
        this.client = client;
    }

    @Override
    public Optional<T> findOne(final String key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public Stream<T> findAll() {
        Set<String> keys = stream(client.listCollection(collection)
                .limit(LIMIT)
                .withValues(false)
                .get(tClass)
                .get().spliterator(), false).map(KvObject::getKey).collect(Collectors.toSet());
        return find(keys);
    }

    @Override
    public Stream<T> find(String query) {
        Set<String> keys = stream(client.searchCollection(collection)
                .get(tClass, query)
                .get().spliterator(), false).map(Result::getKvObject).map(KvObject::getKey).collect(Collectors.toSet());
        return find(keys);
    }

    @Override
    public void save(final T entity) {
        put(entity.getKey(), entity);
    }

    @Override
    @CacheRemove(afterInvocation = false)
    public void delete(@CacheKey final String key) {
        client.kv(collection, key)
                .delete(true)
                .get();
    }

    @CachePut(afterInvocation = true)
    public void put(@CacheKey String key, @CacheValue T entity) {
        LOGGER.info("Writing out to orchestrate: " + entity);
        client.kv(collection, key)
                .put(entity)
                .get();
        LOGGER.info("Thinks its: " + get(key));
    }

    @CacheResult
    public T get(@CacheKey String key) {
        KvObject<T> categoryKvObject = client.kv(collection, key)
                .get(tClass)
                .get();
        return categoryKvObject == null ? null : categoryKvObject.getValue(tClass);
    }

    private Stream<T> find(Set<String> keys) {
        return keys.stream().map(this::get).filter(e -> e != null);
    }

    public String getCollection() {
        return collection;
    }
}
