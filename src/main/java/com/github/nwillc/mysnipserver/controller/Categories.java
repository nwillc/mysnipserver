package com.github.nwillc.mysnipserver.controller;

import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class Categories extends Controller {
	public Categories() {
		getRoutes().put("/v1/categories", this::findAll);
	}

	public List<String> findAll(Request request, Response response) {
		List<String> list = new ArrayList<>();
		list.add("Java");
		list.add("glossery");
		return list;
	}
}
