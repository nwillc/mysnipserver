package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.entity.Category;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.nwillc.myorchsnip.dao.Dao;

public class Categories implements SparkController {
   	private final Dao<Category> dao;

	public Categories(Dao<Category> dao) {
		this.dao = dao;
		get("categories", this::findAll);
	}

	public List<Category> findAll(Request request, Response response) {
		return dao.findAll().collect(Collectors.toList());
	}

	public Boolean save(Request request, Response response) {
		return Boolean.TRUE;
	}
}
