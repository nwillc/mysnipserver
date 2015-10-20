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
import com.github.nwillc.mysnipserver.entity.Category;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Categories implements SparkController {
	private final static Logger LOGGER = Logger.getLogger(Categories.class.getCanonicalName());
   	private final Dao<Category> dao;

	public Categories(Dao<Category> dao) {
		this.dao = dao;
		get("categories", this::findAll);
		post("categories", this::save);
	}

	public List<Category> findAll(Request request, Response response) {
		return dao.findAll().collect(Collectors.toList());
	}

	public Boolean save(Request request, Response response) {
		try {
			final Category category = mapper.get().readValue(request.body(), Category.class);
			LOGGER.info("Category: " + category);
			dao.save(category);
			return Boolean.TRUE;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}
}
