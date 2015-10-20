/*
 * Copyright (c) 2015,  nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.mysnipserver.controller;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.rest.HttpStatusCode;
import com.github.nwillc.mysnipserver.rest.error.HttpException;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.github.nwillc.mysnipserver.rest.Params.CATEGORY;
import static com.github.nwillc.mysnipserver.rest.Params.KEY;
import static com.github.nwillc.mysnipserver.rest.Params.TITLE;

public class Snippets implements SparkController {
	private final Dao<Snippet> dao;
	private final static Logger LOGGER = Logger.getLogger(Snippets.class.getCanonicalName());

	public Snippets(Dao<Snippet> dao) {
		this.dao = dao;
		get("snippets", this::findAll);
		get("snippets/category/" + CATEGORY.getLabel(), this::find);
		get("snippets/category/" + CATEGORY.getLabel() + "/title/" + TITLE.getLabel(), this::findOne);
		post("snippets", this::save);
		delete("snippets/" + KEY.getLabel(), this::delete);
	}

	public List<Snippet> findAll(Request request, Response response) {
		LOGGER.info("Requesting all");
		return dao.findAll().collect(Collectors.toList());
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

	public Boolean delete(Request request, Response response) {
		dao.delete(KEY.from(request));
		return Boolean.TRUE;
	}

	public Boolean save(Request request, Response response) {
		try {
			final Snippet snippet = mapper.get().readValue(request.body(), Snippet.class);
			LOGGER.info("Saving snippet: " + snippet);
			dao.save(snippet);
			return Boolean.TRUE;
		} catch (Exception e) {
			throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Failed saving snippet");
		}
	}
}
