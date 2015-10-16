package com.github.nwillc.mysnipserver.rest;

public interface Version {
	String API_VERSION = "v1";
	default String versionedPath(String path) {
		return "/" + API_VERSION + "/" + path;
	}
}
