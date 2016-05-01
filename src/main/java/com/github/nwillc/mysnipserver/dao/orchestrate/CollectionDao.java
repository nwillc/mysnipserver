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

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class CollectionDao<T extends Entity> implements Dao<T> {
    private static final Logger LOGGER = Logger.getLogger(CollectionDao.class.getName());
    private static final CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
    private static final int LIMIT = 100;
    private final Class<T> tClass;
    private final String collection;
    private final Client client;
    private final Cache<String, T> cache;

    public CollectionDao(Client client, Class<T> tClass) {
        this(client, tClass.getSimpleName(), tClass);
    }

    public CollectionDao(Client client, String collection, Class<T> tClass) {
        this.collection = collection;
        this.tClass = tClass;
        this.client = client;
        cache = getCache();
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
        LOGGER.info("Writing out to orchestrate: " + entity);
        client.kv(collection, entity.getKey())
                .put(entity)
                .get();
        cache.put(entity.getKey(), entity);
        LOGGER.info("Thinks its: " + get(entity.getKey()));
    }

    @Override
    public void delete(final String key) {
        client.kv(collection, key)
                .delete(true)
                .get();
        cache.remove(key);
    }

    public T get(String key) {
        return cache.get(key);
    }

    private Stream<T> find(Set<String> keys) {
        return keys.stream().map(this::get).filter(e -> e != null);
    }

    public String getCollection() {
        return collection;
    }

    @SuppressWarnings("unchecked")
    private Cache<String, T> getCache() {
        MutableConfiguration<String, T> configuration = new MutableConfiguration<>();
        configuration.setTypes(String.class, tClass);
        configuration.setStoreByValue(false);
        configuration.setReadThrough(true);
        configuration.setCacheLoaderFactory(FactoryBuilder.factoryOf(OrchestrateLoader.class));
        return cacheManager.createCache(collection, configuration);
    }

    private class OrchestrateLoader implements CacheLoader<String, T> {
        @Override
        public T load(String key) throws CacheLoaderException {
            KvObject<T> categoryKvObject = client.kv(collection, key)
                    .get(tClass)
                    .get();
            return categoryKvObject == null ? null : categoryKvObject.getValue(tClass);
        }

        @Override
        public Map<String, T> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
            return null;
        }
    }
}
