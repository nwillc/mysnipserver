package com.github.nwillc.mysnipserver.entity;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;

import java.util.List;

import static com.github.nwillc.mysnipserver.controller.graphql.schema.SnippetSchema.DATASTORE;


@GraphQLName(DATASTORE)
public interface DataStore {
    @GraphQLField
    List<Category> getCategories();
    @GraphQLField
    List<Snippet> getSnippets();
}
