package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.myorchsnip.dao.Entity;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 *
 */
public class MemoryBackedDaoTest {
	private Dao<TestEntity> testEntityDao = new MemoryBackedDao<>();

	@Test
	public void shouldDelete() throws Exception {
		testEntityDao.save(new TestEntity("foo"));
		assert(testEntityDao.findOne("foo").get().getKey()).equals("foo");
		testEntityDao.delete("foo");
		testEntityDao.findOne("foo").ifPresent(one -> {
			fail("Entity foo still present");
		});
	}

	@Test
	public void shouldFindAll() throws Exception {
		testEntityDao.save(new TestEntity("foo"));
		testEntityDao.save(new TestEntity("bar"));
		assertThat(testEntityDao.findAll().map(testEntity -> testEntity.getKey()).collect(Collectors.toList())).contains("foo", "bar");
	}

	@Test
	public void shouldSaveAndFindOne() throws Exception {
		testEntityDao.findOne("foo").ifPresent(one -> {
			fail("Entity foo already present");
		});
		testEntityDao.save(new TestEntity("foo"));
		assert(testEntityDao.findOne("foo").get().getKey()).equals("foo");
	}

	private class TestEntity extends Entity {
		private final String key;

		public TestEntity(String key) {
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}
	}
}