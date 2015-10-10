package com.github.nwillc.mysnipserver.controller;

import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class Categories {
	public List<String> findAll(Request request, Response response) {
		List<String> list = new ArrayList<>();
		list.add("Java");
		return list;
	}
}
