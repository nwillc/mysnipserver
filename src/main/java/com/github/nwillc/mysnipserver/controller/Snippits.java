package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.mysnipserver.Entities.Snippet;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import static com.github.nwillc.mysnipserver.rest.Params.*;

public class Snippits implements SparkController {
	private final static Logger LOGGER = Logger.getLogger(Snippits.class.getCanonicalName());

	public Snippits() {
		get("snippets/category/" + CATEGORY.getLabel(), this::find);
		get("snippets/category/" + CATEGORY.getLabel() + "/title/" + TITLE.getLabel(), this::findOne);
	}

	public List<String> find(Request request, Response response) {
		LOGGER.info("Finding snippets in category: " + CATEGORY.from(request));
		List<String> list = new ArrayList<>();
		list.add("This is a test");
		list.add("Another");
		return list;
	}

	public Snippet findOne(Request request, Response response) {
		LOGGER.info("Finding snippet in category " + CATEGORY.from(request) + " entitled " + TITLE.from(request));
		return new Snippet("Java", "Another", "this is another \n snippet.");
	}
}
