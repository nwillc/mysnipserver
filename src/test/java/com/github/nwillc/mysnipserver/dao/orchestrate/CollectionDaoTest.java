package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.IntegrationTest;
import com.github.nwillc.mysnipserver.entity.Snippet;
import io.orchestrate.client.Client;
import io.orchestrate.client.OrchestrateClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(IntegrationTest.class)
public class CollectionDaoTest {
	private static final String ORCH_API_KEY = "57bd3ce4-e827-4d62-a541-01380dd61c86";
	private Client client;
	private CollectionDao<Snippet> dao;

	@Before
	public void setUp() throws Exception {
		client = new OrchestrateClient(ORCH_API_KEY);
		dao = new CollectionDao<>(client, "Snippet", Snippet.class);
	}

	@Test
	public void shouldFindAll() throws Exception {
	  	long c;
		c = dao.findAll().count();
		c = dao.findAll().count();
	}
}