package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.mysnipserver.entity.Category;

public class CategoryDao extends MemoryBackedDao<Category> {

	public CategoryDao() {
		save(new Category("Java"));
		save(new Category("Shell Script"));
	}

}
