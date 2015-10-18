package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Snippet;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.github.nwillc.mysnipserver.rest.Params.CATEGORY;
import static com.github.nwillc.mysnipserver.rest.Params.TITLE;

public class Snippits implements SparkController {
	private final Dao<Snippet> dao;
	private final static Logger LOGGER = Logger.getLogger(Snippits.class.getCanonicalName());

	public Snippits(Dao<Snippet> dao) {
		this.dao = dao;
		get("snippets/category/" + CATEGORY.getLabel(), this::find);
		get("snippets/category/" + CATEGORY.getLabel() + "/title/" + TITLE.getLabel(), this::findOne);
	}

	public List<Snippet> find(Request request, Response response) {
		LOGGER.info("Finding snippets in category: " + CATEGORY.from(request));
		return dao.findAll().filter(snippet -> CATEGORY.from(request).equals(snippet.getCategory())).collect(Collectors.toList());
	}

	public Snippet findOne(Request request, Response response) {
		LOGGER.info("Finding body in category " + CATEGORY.from(request) + " entitled " + TITLE.from(request));
		return dao.findAll().filter(
				snippet -> CATEGORY.from(request).equals(snippet.getCategory()) &&
							TITLE.from(request).equals(snippet.getTitle())
			).findFirst().get();
	}
}
