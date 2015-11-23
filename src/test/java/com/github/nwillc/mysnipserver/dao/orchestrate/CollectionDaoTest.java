package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.IntegrationTest;
import com.github.nwillc.mysnipserver.entity.Snippet;
import io.orchestrate.client.Client;
import io.orchestrate.client.OrchestrateClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;


@Category(IntegrationTest.class)
public class CollectionDaoTest {
	private static final String ORCH_API_KEY = System.getenv("ORCH_API_KEY");
	private CollectionDao<Snippet> dao;

	@Before
	public void setUp() throws Exception {
		Client client = new OrchestrateClient(ORCH_API_KEY);
		dao = new CollectionDao<>(client, "Snippet", Snippet.class);
	}

	@Test
	public void shouldFindAll() throws Exception {
	  	long c = dao.findAll().count();
		assertThat(dao.getCache()).hasSize((int)c);
	}
}