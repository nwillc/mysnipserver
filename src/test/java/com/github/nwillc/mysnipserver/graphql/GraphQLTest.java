/*
 * Copyright (c) 2017, nwillc@gmail.com
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

package com.github.nwillc.mysnipserver.graphql;


import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.query.Query;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(JMockit.class)
@SuppressWarnings("unchecked")
public class GraphQLTest {
    public static final String CATEGORY = "CATEGORY";
    public static final String CATEGORIES = "categories";
    public static final String TITLE = "TITLE";
    public static final String BODY = "BODY";
    public static final String KEY = "KEY";
    public static final String SNIPPET = "snippet";
    public static final String SNIPPETS = "snippets";
    public static final String NAME = "NAME";
    private GraphQLSchema schema;
    private GraphQL graphQL;

    @Mocked
    private Dao<String, Snippet> snippetDao;
    @Mocked
    private Dao<String, Category> categoryDao;


    @Before
    public void setUp() throws Exception {
        SchemaParser schemaParser = new SchemaParser();
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("snippets.graphqls");
        assertThat(inputStream).isNotNull();
        final InputStreamReader streamReader = new InputStreamReader(inputStream);
        assertThat(streamReader).isNotNull();
        final TypeDefinitionRegistry registry = schemaParser.parse(streamReader);
        assertThat(registry).isNotNull();
        final RuntimeWiring wiring = RuntimeWiringBuilder.getRuntimeWiring(snippetDao, categoryDao);
        assertThat(wiring).isNotNull();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        schema = schemaGenerator.makeExecutableSchema(registry, wiring);
        assertThat(schema).isNotNull();
        graphQL = GraphQL.newGraphQL(schema).build();
        assertThat(graphQL).isNotNull();
    }

    @Test
    public void testSnippetQuery() throws Exception {

        Snippet snippet = new Snippet(CATEGORY, TITLE, BODY);
        snippet.setKey(KEY);
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(
                String.format("query { %s( %s: \"%s\" ) { %s, %s, %s, %s } }", SNIPPET.toLowerCase(), KEY.toLowerCase(), KEY,
                        KEY.toLowerCase(), CATEGORY.toLowerCase(), TITLE.toLowerCase(), BODY.toLowerCase()))
                .build();

        assertThat(executionInput).isNotNull();

        new Expectations() {{
            snippetDao.findOne(KEY);
            result = Optional.of(snippet);
        }};

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPET);
        data = (Map) data.get(SNIPPET);
        assertThat(data).contains(entry(KEY.toLowerCase(), KEY), entry(CATEGORY.toLowerCase(), CATEGORY),
                entry(TITLE.toLowerCase(), TITLE), entry(BODY.toLowerCase(), BODY));
    }

    @Test
    public void testSnippetVariableQuery() throws Exception {

        Snippet snippet = new Snippet(CATEGORY, TITLE, BODY);
        snippet.setKey(KEY);
        Map<String, Object> variables = new HashMap<>();
        variables.put(KEY.toLowerCase(), KEY);
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(
                String.format("query($key: ID!) { %s( %s: $key ) { %s, %s, %s, %s } }",
                        SNIPPET.toLowerCase(), KEY.toLowerCase(),
                        KEY.toLowerCase(), CATEGORY.toLowerCase(),
                        TITLE.toLowerCase(), BODY.toLowerCase()))
                .variables(variables)
                .build();

        assertThat(executionInput).isNotNull();

        new Expectations() {{
            snippetDao.findOne(KEY);
            result = Optional.of(snippet);
        }};

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPET);
        data = (Map) data.get(SNIPPET);
        assertThat(data).contains(entry(KEY.toLowerCase(), KEY), entry(CATEGORY.toLowerCase(), CATEGORY),
                entry(TITLE.toLowerCase(), TITLE), entry(BODY.toLowerCase(), BODY));
    }

    @Test
    public void testSnippetsQuery() throws Exception {

        Snippet snippet1 = new Snippet(CATEGORY, TITLE, BODY);
        snippet1.setKey("1");

        Snippet snippet2 = new Snippet(CATEGORY, TITLE, BODY);
        snippet2.setKey("2");

        List<Snippet> snippetList = new ArrayList<>();
        snippetList.add(snippet1);
        snippetList.add(snippet2);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(
                String.format("query { %s { %s, %s, %s, %s } }", SNIPPETS.toLowerCase(),
                        KEY.toLowerCase(), CATEGORY.toLowerCase(), TITLE.toLowerCase(), BODY.toLowerCase()))
                .build();

        assertThat(executionInput).isNotNull();

        new Expectations() {{
            snippetDao.findAll();
            result = snippetList.stream();
        }};

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPETS);
        List<Map> list = (List<Map>) data.get(SNIPPETS);

        Integer key = 1;
        for (Map element : list) {
            assertThat(element).contains(entry(KEY.toLowerCase(), key.toString()), entry(CATEGORY.toLowerCase(), CATEGORY),
                    entry(TITLE.toLowerCase(), TITLE), entry(BODY.toLowerCase(), BODY));
            key++;
        }
    }

    @Test
    public void testSnippetsInCategoryQuery() throws Exception {

        Snippet snippet1 = new Snippet(CATEGORY + "1", TITLE, BODY);
        snippet1.setKey("1");

        Snippet snippet2 = new Snippet(CATEGORY + "2", TITLE, BODY);
        snippet2.setKey("2");

        List<Snippet> snippetList = new ArrayList<>();
        snippetList.add(snippet1);
        snippetList.add(snippet2);

        Map<String,Object> variables = new HashMap<>();
        variables.put(CATEGORY.toLowerCase(), CATEGORY + "1");

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("query($category: String) {snippets( category: $category ) { key category title body } }")
                .variables(variables)
                .build();

        assertThat(executionInput).isNotNull();

        new Expectations() {{
            snippetDao.find((Query)any);
            result = snippetList.stream();
        }};

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPETS);
        List<Map> list = (List<Map>) data.get(SNIPPETS);

//        assertThat(list).hasSize(1);
//        Map element = list.get(0);
//        assertThat(element).contains(entry(KEY.toLowerCase(), "1"),
//                entry(CATEGORY.toLowerCase(), CATEGORY + "1"),
//                entry(TITLE.toLowerCase(), TITLE), entry(BODY.toLowerCase(), BODY));
    }

    @Test
    public void testCategoryQuery() throws Exception {

        final Category category = new Category(NAME);
        category.setKey(KEY);
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(
                String.format("query { %s( %s: \"%s\" ) { %s, %s } }", CATEGORY.toLowerCase(), KEY.toLowerCase(), KEY,
                        KEY.toLowerCase(), NAME.toLowerCase()))
                .build();

        assertThat(executionInput).isNotNull();

        new Expectations() {{
            categoryDao.findOne(KEY);
            result = Optional.of(category);
        }};

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(CATEGORY.toLowerCase());
        data = (Map) data.get(CATEGORY.toLowerCase());
        assertThat(data).contains(entry(KEY.toLowerCase(), KEY), entry(NAME.toLowerCase(), NAME));
    }

    @Test
    public void testCategoriesQuery() throws Exception {

        Category category1 = new Category(NAME);
        category1.setKey("1");

        Category category2 = new Category(NAME);
        category2.setKey("2");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(
                String.format("query { %s { %s, %s } }", CATEGORIES.toLowerCase(),
                        KEY.toLowerCase(), NAME.toLowerCase()))
                .build();

        assertThat(executionInput).isNotNull();

        new Expectations() {{
            categoryDao.findAll();
            result = categoryList.stream();
        }};

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(CATEGORIES);
        List<Map> list = (List<Map>) data.get(CATEGORIES);

        Integer key = 1;
        for (Map element : list) {
            assertThat(element).contains(entry(KEY.toLowerCase(), key.toString()), entry(NAME.toLowerCase(), NAME));
            key++;
        }
    }
}
