package com.github.nwillc.mysnipserver.Entities;

public class Snippet {
	public String category;
	public String title;
	public String snippet;

	public Snippet(String category, String title, String snippet) {
		this.snippet = snippet;
		this.category = category;
		this.title = title;
	}
}
