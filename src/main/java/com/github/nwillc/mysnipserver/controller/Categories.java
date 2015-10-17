package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.entity.Category;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;
import com.github.nwillc.myorchsnip.dao.Dao;

public class Categories implements SparkController {
   	private final Dao<Category> dao;

	public Categories(Dao<Category> dao) {
		this.dao = dao;
		get("categories", this::findAll);
	}

	public List<String> findAll(Request request, Response response) {
		List<String> list = new ArrayList<>();
		list.add("Java");
		list.add("glossery");
		return list;
	}

	public Boolean save(Request request, Response response) {
		return Boolean.TRUE;
	}
}
