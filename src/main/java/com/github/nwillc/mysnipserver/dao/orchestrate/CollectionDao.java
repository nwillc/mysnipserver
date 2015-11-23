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

package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.dao.Entity;
import com.github.nwillc.simplecache.SCache;
import com.github.nwillc.simplecache.integration.SCacheLoader;
import com.github.nwillc.simplecache.integration.SCacheWriter;
import com.github.nwillc.simplecache.managment.SCacheStatisticsMXBean;
import io.orchestrate.client.Client;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.Result;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CompletionListenerFuture;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class CollectionDao<T extends Entity> implements Dao<T> {
    private static final Logger LOGGER = Logger.getLogger(CollectionDao.class.getCanonicalName());
    private static final int LIMIT = 100;
    private static final int STATS_REPORT_SECONDS = 30;
    private static final boolean REPLACE_FALSE = false;
    private final Class<T> tClass;
    private final String collection;
    private final Client client;
    private final Cache<String, T> cache;
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    public CollectionDao(Client client, Class<T> tClass) {
        this(client, tClass.getSimpleName(), tClass);
    }

    public CollectionDao(Client client, String collection, Class<T> tClass) {
        this.collection = collection;
        this.tClass = tClass;
        this.client = client;
        cache = cacheSetup();
    }

    @Override
    public Optional<T> findOne(final String key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Stream<T> findAll() {
        if (loaded.compareAndSet(false, true)) {
            Set<String> keys = stream(client.listCollection(collection)
                    .limit(LIMIT)
                    .withValues(false)
                    .get(tClass)
                    .get().spliterator(), false).map(KvObject::getKey).collect(Collectors.toSet());
            return find(keys);
        } else {
           return stream(cache.spliterator(), false).map(Cache.Entry::getValue);
        }
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
        cache.put(entity.getKey(), entity);
    }

    @Override
    public void delete(final String key) {
        cache.remove(key);
    }

    private Stream<T> find(Set<String> keys) {
        CompletionListenerFuture done = new CompletionListenerFuture();
        cache.loadAll(keys, REPLACE_FALSE, done);
        try {
            done.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.warning("Failed finding a set: " + e.getMessage());
        }
        return cache.getAll(keys).values().stream();
    }

    private void deleteThrough(Object key) {
        client.kv(collection, key.toString())
                .delete(true)
                .get();
    }

    private void writeThrough(Cache.Entry entry) {
        client.kv(collection, entry.getKey().toString())
                .put(entry.getValue())
                .get();
    }

    private T readThrough(String key) {
        KvObject<T> categoryKvObject = client.kv(collection, key)
                .get(tClass)
                .get();
        return categoryKvObject == null ? null : categoryKvObject.getValue(tClass);
    }

    Cache<String,T>  getCache() {
        return cache;
    }

    private Cache<String, T> cacheSetup() {
        MutableConfiguration<String, T> configuration = new MutableConfiguration<>();

        configuration.setReadThrough(true);
        configuration.setCacheLoaderFactory((Factory<CacheLoader<String, T>>) () ->
                new SCacheLoader<>(this::readThrough));


        configuration.setWriteThrough(true);
        configuration.setCacheWriterFactory((Factory<CacheWriter<String, T>>) () ->
                new SCacheWriter<>(this::deleteThrough, this::writeThrough));

        configuration.setExpiryPolicyFactory(() -> new TouchedExpiryPolicy(new Duration(TimeUnit.MINUTES, 10)));
        configuration.setStatisticsEnabled(true);
        Cache<String, T> c = Caching.getCachingProvider().getCacheManager().createCache(collection, configuration);
        SCache sCache = c.unwrap(SCache.class);
        SCacheStatisticsMXBean statistics = sCache.getStatistics();
        new Timer(collection, true)
                .schedule(new TimerTask() {
                              @Override
                              public void run() {
                                  LOGGER.info(collection + "-" + statistics.toString());
                              }
                          },
                        TimeUnit.SECONDS.toMillis(STATS_REPORT_SECONDS),
                        TimeUnit.SECONDS.toMillis(STATS_REPORT_SECONDS));
        return c;
    }

}
