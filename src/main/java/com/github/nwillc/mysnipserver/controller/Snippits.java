package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.Entities.Snippet;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class Snippits implements SparkController {
	public Snippits() {
		get("/v1/snippets/category/*", this::find);
		get("/v1/snippet/category/*/title/*", this::findOne);
	}

	public List<String> find(Request request, Response response) {
		List<String> list = new ArrayList<>();
		list.add("This is a test");
		list.add("Another");
		return list;
	}

	public Snippet findOne(Request request, Response response) {
		return new Snippet("Java", "Another", "this is another \n snippet.");
	}
}
