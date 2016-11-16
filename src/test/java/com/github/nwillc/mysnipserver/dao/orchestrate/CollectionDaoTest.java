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

package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.entity.Snippet;
import io.orchestrate.client.Client;
import io.orchestrate.client.OrchestrateClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import javax.cache.Cache;
import javax.cache.Caching;

import static org.assertj.core.api.Assertions.assertThat;


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

	@Ignore
	@Test
	public void shouldFindAll() throws Exception {
		long c = dao.findAll().count();
		assertThat(c).isGreaterThan(0L);
		assertThat(cache).hasSize((int) c);
	}

	@Ignore
	@Test
	public void shouldCachePut() throws Exception {
		Snippet snippet = new Snippet("Test", "A test", "test body");
		dao.save(snippet);
	}
}