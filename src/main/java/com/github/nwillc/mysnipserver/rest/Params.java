package com.github.nwillc.mysnipserver.rest;

public enum  Params {
	CATEGORY,
	TITLE;

	public String getLabel() {
		return ":" + name().toLowerCase();
	}
}
