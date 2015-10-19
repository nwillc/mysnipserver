package com.github.nwillc.mysnipserver.rest;

import spark.Request;

public enum  Params {
	CATEGORY,
	KEY,
	TITLE;

	public String getLabel() {
		return ":" + name().toLowerCase();
	}

	public String from(Request request) {
		return request.params(getLabel());
	}
}
