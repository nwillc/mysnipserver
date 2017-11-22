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


import com.github.nwillc.mysnipserver.dao.jdbc.CategoryConfiguration;
import com.github.nwillc.mysnipserver.dao.jdbc.SnippetConfiguration;
import com.github.nwillc.mysnipserver.dao.jdbc.TestDatabase;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.util.JsonMapper;
import com.github.nwillc.opa.impl.jdbc.JdbcDao;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.pmw.tinylog.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

@SuppressWarnings("unchecked")
public class GraphQLTest implements JsonMapper {
    public static final String CATEGORY = "category";
    public static final String CATEGORIES = "categories";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String KEY = "key";
    public static final String SNIPPET = "snippet";
    public static final String SNIPPETS = "snippets";
    public static final String NAME = "name";
    private GraphQL graphQL;

    private static final Category CATEGORY_A = new Category("A");
    private static final Category CATEGORY_B = new Category("B");
    private static final Snippet SNIPPET_A_ONE = new Snippet(CATEGORY_A.getKey(), "one", "A one");
    private static final Snippet SNIPPET_A_TWO = new Snippet(CATEGORY_A.getKey(), "two", "A two");
    private static final Snippet SNIPPET_B_THREE = new Snippet(CATEGORY_B.getKey(), "three", "B one");
    private static final Snippet SNIPPET_B_FOUR = new Snippet(CATEGORY_B.getKey(), "four", "B two");
    private JdbcDao<String, Snippet> snippetJdbcDao;
    private JdbcDao<String, Category> categoryJdbcDao;

    @Rule
    public TestDatabase testDatabase = new TestDatabase();


    @Before
    public void setUp() throws Exception {
        // Set up dummy data
        categoryJdbcDao = new JdbcDao<>(new CategoryConfiguration(testDatabase.getDatabase()));
        categoryJdbcDao.save(CATEGORY_A);
        categoryJdbcDao.save(CATEGORY_B);

        snippetJdbcDao = new JdbcDao<>(new SnippetConfiguration(testDatabase.getDatabase()));
        snippetJdbcDao.save(SNIPPET_A_ONE);
        snippetJdbcDao.save(SNIPPET_A_TWO);
        snippetJdbcDao.save(SNIPPET_B_THREE);
        snippetJdbcDao.save(SNIPPET_B_FOUR);

        // Setup GraphQL
        SchemaParser schemaParser = new SchemaParser();
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("snippets.graphqls");
        final InputStreamReader streamReader = new InputStreamReader(inputStream);
        final TypeDefinitionRegistry registry = schemaParser.parse(streamReader);
        final RuntimeWiring wiring = RuntimeWiringBuilder.getRuntimeWiring(snippetJdbcDao, categoryJdbcDao);
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema schema = schemaGenerator.makeExecutableSchema(registry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    @Test
    public void testCategoryQuery() throws Exception {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("query { category( key: \"%s\" ) { key name } }", CATEGORY_A.getKey()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(CATEGORY);
        data = (Map) data.get(CATEGORY);
        assertThat(data).contains(
                entry(KEY, CATEGORY_A.getKey()),
                entry(NAME, CATEGORY_A.getName()));
    }

    @Test
    public void testCategoryCreate() throws Exception {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("mutation { category( name: \"foo\" ) { key name } }")
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(CATEGORY);
        data = (Map) data.get(CATEGORY);
        assertThat(data).containsKeys(KEY, NAME);
        assertThat(data.get(NAME)).isEqualTo("foo");

        assertThat(categoryJdbcDao.findOne(data.get(KEY).toString())).isPresent();
    }

    @Test
    public void testCategoryUpdate() throws Exception {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("mutation { category( key: \"%s\" name: \"foo\" ) { key name } }", CATEGORY_A.getKey()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(CATEGORY);
        data = (Map) data.get(CATEGORY);
        assertThat(data).containsKeys(KEY, NAME);
        assertThat(data.get(KEY)).isEqualTo(CATEGORY_A.getKey());
        assertThat(data.get(NAME)).isEqualTo("foo");

        assertThat(categoryJdbcDao.findOne(data.get(KEY).toString())).isPresent();
    }

    @Test
    public void testDeleteCategory() throws Exception {
        assertThat(categoryJdbcDao.findOne(CATEGORY_A.getKey())).isPresent();
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("mutation { deleteCategory( key: \"%s\" ) }", CATEGORY_A.getKey()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys("deleteCategory");
        Boolean success = (Boolean) data.get("deleteCategory");
        assertThat(success).isTrue();
        assertThat(categoryJdbcDao.findOne(CATEGORY_A.getKey())).isNotPresent();
    }

    @Test
    public void testCategoriesQuery() throws Exception {

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("query { categories { key name } }")
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(CATEGORIES);
        List<Map> list = (List<Map>) data.get(CATEGORIES);
        assertThat(list).hasSize(2);
        list.forEach(element -> assertThat(element.get(KEY)).isIn(CATEGORY_A.getKey(), CATEGORY_B.getKey()));
    }

    @Test
    public void testCategoryMatch() throws Exception {

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("query { categories( match: \"%s\") { key name } }", CATEGORY_A.getName()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(CATEGORIES);
        List<Map> list = (List<Map>) data.get(CATEGORIES);
        assertThat(list).hasSize(1);
        list.forEach(element -> assertThat(element.get(KEY)).isIn(CATEGORY_A.getKey()));
    }

    @Test
    public void testSnippetQuery() throws Exception {

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("query { snippet( key: \"%s\" ) { key category title body } }", SNIPPET_A_ONE.getKey()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPET);
        data = (Map) data.get(SNIPPET);
        assertThat(data).contains(
                entry(KEY, SNIPPET_A_ONE.getKey()),
                entry(CATEGORY, SNIPPET_A_ONE.getCategory()),
                entry(TITLE, SNIPPET_A_ONE.getTitle()),
                entry(BODY, SNIPPET_A_ONE.getBody()));
    }

    @Test
    public void testSnippetCreate() throws Exception {
        final Snippet snippet = new Snippet(CATEGORY_A.getKey(), "title", "body");
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("mutation { snippet( category: \"%s\" title: \"%s\" body: \"%s\") { key category title body } }",
                        snippet.getCategory(), snippet.getTitle(), snippet.getBody()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPET);
        data = (Map) data.get(SNIPPET);
        assertThat(data).contains(
                entry(CATEGORY, snippet.getCategory()),
                entry(TITLE, snippet.getTitle()),
                entry(BODY, snippet.getBody()));
        assertThat(snippetJdbcDao.findOne(data.get(KEY).toString())).isPresent();
    }

    @Test
    public void testSnippetUpdate() throws Exception {
        final Snippet snippet = new Snippet("category", "title", "body");
        snippet.setKey(SNIPPET_A_ONE.getKey());
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("mutation { snippet( key: \"%s\" category: \"%s\" title: \"%s\" body: \"%s\") { key category title body } }",
                 snippet.getKey(), snippet.getCategory(), snippet.getTitle(), snippet.getBody()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPET);
        data = (Map) data.get(SNIPPET);
        assertThat(data).contains(
                entry(KEY, snippet.getKey()),
                entry(CATEGORY, snippet.getCategory()),
                entry(TITLE, snippet.getTitle()),
                entry(BODY, snippet.getBody()));
    }

    @Test
    public void testSnippetDelete() throws Exception {
        assertThat(snippetJdbcDao.findOne(SNIPPET_A_ONE.getKey())).isPresent();
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("mutation { deleteSnippet( key: \"%s\" ) }", SNIPPET_A_ONE.getKey()))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys("deleteSnippet");
        Boolean success = (Boolean) data.get("deleteSnippet");
        assertThat(success).isTrue();
        assertThat(snippetJdbcDao.findOne(SNIPPET_A_ONE.getKey())).isNotPresent();
    }

    @Test
    public void testSnippetDeleteNotFound() throws Exception {
        final String badKey = SNIPPET_A_ONE.getKey() + "bad";
        assertThat(snippetJdbcDao.findOne(badKey)).isNotPresent();
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("mutation { deleteSnippet( key: \"%s\" ) }", badKey))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys("deleteSnippet");
        Boolean success = (Boolean) data.get("deleteSnippet");
        assertThat(success).isTrue();
    }

    @Test
    public void testSnippetVariableQuery() throws Exception {
        final Map<String, Object> variables = new HashMap<>();
        variables.put("key", SNIPPET_A_TWO.getKey());
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("query($key: ID!) { snippet( key: $key ) { key category title body } }")
                .variables(variables)
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPET);
        data = (Map) data.get(SNIPPET);
        assertThat(data).contains(
                entry(KEY, SNIPPET_A_TWO.getKey()),
                entry(CATEGORY, SNIPPET_A_TWO.getCategory()),
                entry(TITLE, SNIPPET_A_TWO.getTitle()),
                entry(BODY, SNIPPET_A_TWO.getBody()));
    }

    @Test
    public void testSnippetsQuery() throws Exception {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("query { snippets { key category title body } }")
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPETS);
        List<Map> list = (List<Map>) data.get(SNIPPETS);

        assertThat(list).hasSize(4);
        list.forEach(element -> assertThat(element.get(KEY)).isIn(SNIPPET_A_ONE.getKey(), SNIPPET_A_TWO.getKey(), SNIPPET_B_THREE.getKey(), SNIPPET_B_FOUR.getKey()));
    }

    @Test
    public void testSnippetsMatch() throws Exception {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("query { snippets( match: \"%s\") { key category title body } }", "one"))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPETS);
        List<Map> list = (List<Map>) data.get(SNIPPETS);

        assertThat(list).hasSize(2);
    }

    @Test
    public void testSnippetsCategoryAndMatch() throws Exception {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(String.format("query { snippets( category: \"%s\" match: \"%s\") { key category title body } }",
                       CATEGORY_A.getKey(), "one"))
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPETS);
        List<Map> list = (List<Map>) data.get(SNIPPETS);

        assertThat(list).hasSize(1);
        list.forEach(element -> assertThat(element.get(KEY)).isNotEqualTo(SNIPPET_B_THREE.getKey()));
    }

    @Test
    public void testSnippetsInCategoryQuery() throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put(CATEGORY, CATEGORY_A.getKey());

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("query($category: ID) {snippets( category: $category ) { key category title body } }")
                .variables(variables)
                .build();

        assertThat(executionInput).isNotNull();

        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys(SNIPPETS);
        List<Map> list = (List<Map>) data.get(SNIPPETS);
        assertThat(list).hasSize(2);
        list.forEach(element -> assertThat(element.get(KEY)).isIn(SNIPPET_A_ONE.getKey(), SNIPPET_A_TWO.getKey()));
    }

    @Test
    public void testExport() throws Exception {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("{ export { categories { key name }  snippets { key category title body } }}")
                .build();
        assertThat(executionInput).isNotNull();
        final ExecutionResult result = graphQL.execute(executionInput);
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).isEmpty();

        Map data = result.getData();
        assertThat(data).containsKeys("export");
        data = (Map) data.get("export");
        assertThat(data).containsKeys(SNIPPETS, CATEGORIES);
    }
}
