package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.entity.Snippet;
import io.orchestrate.client.Client;
import io.orchestrate.client.OrchestrateClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import javax.cache.Cache;
import javax.cache.Caching;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("integration")
public class CollectionDaoTest {
	private static final String ORCH_API_KEY = System.getenv("ORCH_API_KEY");
	private CollectionDao<Snippet> dao;
	private Cache<String, Snippet> cache;


	@BeforeEach
	public void setUp() throws Exception {
		assertThat(ORCH_API_KEY).isNotEmpty();
		Client client = new OrchestrateClient(ORCH_API_KEY);
		dao = new CollectionDao<>(client, "Snippet", Snippet.class);
		cache = Caching.getCachingProvider().getCacheManager().getCache("Snippet", String.class, Snippet.class);
	}

	@Test
	public void shouldFindAll() throws Exception {
		long c = dao.findAll().count();
		assertThat(c).isGreaterThan(0L);
		assertThat(cache).hasSize((int) c);
	}

	@Test
	public void shouldCachePut() throws Exception {
		Snippet snippet = new Snippet("Test", "A test", "test body");
		dao.save(snippet);
	}
}