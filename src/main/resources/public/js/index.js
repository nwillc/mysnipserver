/*
 * Copyright (c) 2016,  nwillc@gmail.com
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

function gapiAuth2Load() {
    "use strict";
    console.log("Loading gapi auth2");
    gapi.load('auth2', function () {
        gapi.auth2.init();
    });
}

function gapiSignOut() {
    APP.home.logout();
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out of Google.');
    });
}

var APP = APP || {};

APP.Home = function () {
    // Instance variables and initialization
    $('#tabs').tabs();
    this.categories = $('#browseCategories');
    this.snippetCategories = $('#snippetCategories');
    this.moveCategories = $('#moveCategories');
    this.category = $('#category');
    this.titles = $('#titles');
    this.title = $('#title');
    this.body = $('#body');
    this.bodyInput = $('#bodyInput');
    this.query = $('#query');

    this.searchDialog = $('#searchCategoryDialog');
    $(this.searchDialog).dialog({ width: 500 });
    $(this.searchDialog).dialog('close');

    this.moveSnippetDialog = $('#moveSnippetDialog');
    $(this.moveSnippetDialog).dialog({ width: 500 });
    $(this.moveSnippetDialog).dialog('close');

    this.buildInfoDialog = $('#buildInfoDialog');
    $(this.buildInfoDialog).dialog({ height: 200, width: 500 });
    $(this.buildInfoDialog).dialog('close');

    this.graphqlUrl = "v1/graphql";
    this.categoryGQL = new APP.Graphql(this.graphqlUrl, "{ categories { key name }}");
    this.categorySnippetsGQL = new APP.Graphql(this.graphqlUrl, "query($category: String!){ snippets ( category: $category ) { key title }}");
    this.categoryCreateGQL = new APP.Graphql(this.graphqlUrl, "mutation($name: String!){ category(name: $name){ key }}");
    this.snippetBodyGQL = new APP.Graphql(this.graphqlUrl, "query($snippet: String!){ snippet ( key: $snippet) { body }}");
    this.snippetCreateGQL = new APP.Graphql(this.graphqlUrl, "mutation($category: String! $title: String! $body: String!){ snippet ( category: $category title: $title body: $body ){ key }}");
    this.deleteSnippetGQL = new APP.Graphql(this.graphqlUrl, "mutation($snippet: String!) { deleteSnippet ( key: $snippet ) }");
    this.deleteCategoryGQL = new APP.Graphql(this.graphqlUrl, "mutation($category: String!) { deleteCategory ( key: $category ) }");
    this.searchCategoryGQL = new APP.Graphql(this.graphqlUrl, "query($category: String! $match: String!){ snippets( category: $category match: $match ){ key title }}");

    // Functions
    this.loadCategories = () => {
        console.log("loadCategories");
        this.categoryGQL.execute((response) => {
            var list = response.data.categories.sort((a, b) => {
                return a.name.localeCompare(b.name);
            });
            $(this.categories).empty();
            $(this.snippetCategories).empty();
            $(this.moveCategories).empty();
            list.forEach(element => {
                this.categories.append($("<option></option>").attr("value", element.key).text(element.name));
                this.snippetCategories.append($("<option></option>").attr("value", element.key).text(element.name));
                this.moveCategories.append($("<option></option>").attr("value", element.key).text(element.name));
            });

            window.setTimeout(() => {
                $(this.categories).change();
            }, 1);
        });
    };

    this.loadAllTitles = () => {
        this.categorySnippetsGQL.variables["category"] = $(this.categories).val();
        console.log("Snippets for Category");
        this.categorySnippetsGQL.execute((response) => this.loadTitles(response.data.snippets));
    };

    this.loadTitles = (list) => {
        console.log("loadTitles");
        $('option', this.titles).remove();
        $(this.body).val('');
        list.sort((a, b) => a.title.localeCompare(b.title)).forEach(element =>
            this.titles.append($("<option></option>").attr("value", element.key).text(element.title)));
    };

    this.loadBody = () => {
        this.snippetBodyGQL.variables["snippet"] = $(this.titles).val();
        console.log("Requesting snippet");
        this.snippetBodyGQL.execute((response) => $(this.body).val(response.data.snippet.body));
    };

    this.saveCategory = () => {
        console.log("Create Category");
        this.categoryCreateGQL.variables["name"] = $(this.category).val();
        this.categoryCreateGQL.execute(() => {
            this.loadCategories();
            $('#category').val('');
        });
    };

    this.saveSnippet = () => {
        console.log("Create Snippet");
        this.snippetCreateGQL.variables["category"] = $(this.snippetCategories).val();
        this.snippetCreateGQL.variables["title"] = $(this.title).val();
        this.snippetCreateGQL.variables["body"] = $(this.bodyInput).val();
        this.snippetCreateGQL.execute(() => {
            this.loadCategories();
            $(this.title).val('');
            $(this.bodyInput).val('');
        });
    };

    this.deleteSnippet = () => {
        this.deleteSnippetGQL.variables["snippet"] = $(titles).find("option:selected").val();
        console.log("Delete Snippet");
        this.deleteSnippetGQL.execute(() => this.loadCategories());
    };

    this.moveSnippet = () => {
        var selected = $(titles).find("option:selected");
        console.log("Move Snippet: " + $(selected).val());
        $.ajax({
            url: 'v1/snippets/' + $(selected).val() + '/move/' + $(this.moveCategories).val(),
            method: 'PUT',
            success: () => {
                this.loadAllTitles();
                $(this.moveSnippetDialog).dialog('close');
            }
        });
    };

    this.deleteCategory = () => {
        this.deleteCategoryGQL.variables["category"] = $(this.categories).val();
        console.log("Delete Category");
        this.deleteCategoryGQL.execute(() => this.loadCategories());
    };

    this.openSearch = () => {
        $(this.searchDialog).dialog('open');
    };

    this.openMoveSnippet = () => {
        $(this.moveSnippetDialog).dialog('open');
    };

    this.performSearch = () => {
        this.searchCategoryGQL.variables["category"] = $(this.categories).val();
        this.searchCategoryGQL.variables["match"] = $(query).val();
        console.log("Search Category");
        this.searchCategoryGQL.execute((response) => {
            this.loadTitles(response.data.snippets);
            $(this.searchDialog).dialog('close');
        });
    };

    this.updateSnippet = () => {
        console.log("Update Snippet");
        $.post('v1/snippets', JSON.stringify({
            key: $(this.titles).val(),
            category: $(this.categories).val(),
            title: $(this.titles).children(":selected").text(),
            body: $(this.body).val()
        })).fail(() => alert("Failed updating snippet."));
    };

    this.logout = () => {
        console.log("logout");
        $.ajax({
            url: 'v1/auth',
            method: 'DELETE',
            success: () => window.location.replace("/login.html")
        });
    };

    this.buildInfo = () => {
        console.log("build info");
        $.get("properties", data => {
            $(this.buildInfoDialog).dialog('open');
            $('#buildInfo').html(data);
        });
    };

    this.moveDialog = () => {
        this.moveSnippet();
        alert("Yahoooo!");
    };

    // Bindings
    $(this.categories).change(this.loadAllTitles);
    $(this.titles).change(this.loadBody);
    $('#saveSnippetButton').click(this.saveSnippet);
    $('#deleteButton').click(this.deleteSnippet);
    $('#saveCategoryButton').click(this.saveCategory);
    $('#deleteCategoryButton').click(this.deleteCategory);
    $('#searchButton').click(this.openSearch);
    $('#performSearch').click(this.performSearch);
    $('#buildInfoButton').click(this.buildInfo);
    $('#updateButton').click(this.updateSnippet);
    $('#moveButton').click(this.openMoveSnippet);
    $('#logoutButton').click(gapiSignOut);
    $('#performMove').click(this.moveSnippet);

    // Go!
    this.loadCategories();
};

$(document).ready(() => {
    APP.home = new APP.Home();
});
