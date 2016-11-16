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

package com.github.nwillc.mysnipserver.util;


import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;

public final class CacheFactory {
    private static final CacheManager CACHE_MANAGER = Caching.getCachingProvider().getCacheManager();

    private CacheFactory() {}

     public static <T> Cache<String, T> getCache(final String name, final Class<T> clz,
                                                 final Factory<CacheLoader<String,T>> factory) {
        final Cache<String, T> cache = Caching.getCachingProvider().getCacheManager().getCache(name,
                String.class, clz);
        if (cache != null) {
            return cache;
        }
        MutableConfiguration<String, T> configuration = new MutableConfiguration<>();
        configuration.setTypes(String.class, clz);
        configuration.setStoreByValue(false);
        configuration.setReadThrough(true);
         configuration.setCacheLoaderFactory(factory);
        return CACHE_MANAGER.createCache(name, configuration);
    }
}
