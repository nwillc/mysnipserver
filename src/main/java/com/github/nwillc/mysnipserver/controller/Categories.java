package com.github.nwillc.mysnipserver.controller;

import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class Categories implements SparkController {

	public Categories() {
		get("/v1/categories", this::findAll);
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
