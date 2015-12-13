package com.github.nwillc.mysnipserver.dao.orchestrate;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;
import javax.cache.configuration.MutableConfiguration;
import java.lang.annotation.Annotation;

public class ResolverFactory implements CacheResolverFactory {
	private CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

	public CacheResolver getCacheResolver(CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
		return new CacheResolver() {
			@Override
			public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
				Object target = cacheInvocationContext.getTarget();
				Cache cache = null;
				if (target instanceof CollectionDao) {
					String collection = ((CollectionDao) target).getCollection();
					cache = cacheManager.getCache(collection);
					if (cache == null) {
						cache = cacheManager.createCache(collection, new MutableConfiguration<>());
					}
				}
				return cache;
			}
		};
	}

	@Override
	public CacheResolver getExceptionCacheResolver(CacheMethodDetails<CacheResult> cacheMethodDetails) {
		return null;
	}
}
