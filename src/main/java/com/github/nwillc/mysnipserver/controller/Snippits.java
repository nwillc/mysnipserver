package com.github.nwillc.mysnipserver.controller;

import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class Snippits extends Controller {
	public Snippits() {
		getRoutes().put("/v1/snippets/category/*", this::find);
	}

	public List<String> find(Request request, Response response) {
		List<String> list = new ArrayList<>();
		list.add("This is a test");
		list.add("Another");
		return list;
	}
}
