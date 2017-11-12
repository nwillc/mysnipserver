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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(JMockit.class)
public class GraphQLTest {
    public static final String CATEGORY = "CATEGORY";
    public static final String TITLE = "TITLE";
    public static final String BODY = "BODY";
    public static final String KEY = "KEY";
    public static final String SNIPPET = "snippet";
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
        final RuntimeWiring wiring = RuntimeWiringBuilder.getRuntimeWiring(snippetDao);
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
}
