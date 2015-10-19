package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Categories implements SparkController {
	private final static Logger LOGGER = Logger.getLogger(Categories.class.getCanonicalName());
   	private final Dao<Category> dao;

	public Categories(Dao<Category> dao) {
		this.dao = dao;
		get("categories", this::findAll);
		post("categories", this::save);
	}

	public List<Category> findAll(Request request, Response response) {
		return dao.findAll().collect(Collectors.toList());
	}

	public Boolean save(Request request, Response response) {
		try {
			final Category category = mapper.get().readValue(request.body(), Category.class);
			LOGGER.info("Category: " + category);
			dao.save(category);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Boolean.TRUE;
	}
}
