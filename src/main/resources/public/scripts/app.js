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

define(["gapi", "jquery-ui", "jquery", "graphql"], function (gapi, ui, $, graphql) {
    return {
        App: function () {
            "use strict";
            var _this = this;

            // Instance variables and initialization
            $("#tabs").tabs();
            this.categories = $("#browseCategories");
            this.snippetCategories = $("#snippetCategories");
            this.moveCategories = $("#moveCategories");
            this.category = $("#category");
            this.titles = $("#titles");
            this.title = $("#title");
            this.body = $("#body");
            this.bodyInput = $("#bodyInput");
            this.query = $("#query");

            this.searchDialog = $("#searchCategoryDialog");
            $(this.searchDialog).dialog({ width: 500 });
            $(this.searchDialog).dialog("close");

            this.moveSnippetDialog = $("#moveSnippetDialog");
            $(this.moveSnippetDialog).dialog({ width: 500 });
            $(this.moveSnippetDialog).dialog("close");

            this.buildInfoDialog = $("#buildInfoDialog");
            $(this.buildInfoDialog).dialog({ height: 300, width: 500 });
            $(this.buildInfoDialog).dialog("close");

            this.fileImportDialog = $("#fileImportDialog");
            $(this.fileImportDialog).dialog({ height: 100, width: 450 });
            $(this.fileImportDialog).dialog("close");

            var graphqlUrl = "v1/graphql";
            this.categoryGQL = new graphql.Graphql(graphqlUrl, "{ categories { key name }}");
            this.categorySnippetsGQL = new graphql.Graphql(graphqlUrl, "query($category: String!){ snippets ( category: $category ) { key title }}");
            this.categoryCreateGQL = new graphql.Graphql(graphqlUrl, "mutation($name: String!){ category(name: $name){ key }}");
            this.snippetBodyGQL = new graphql.Graphql(graphqlUrl, "query($snippet: String!){ snippet ( key: $snippet) { body }}");
            this.snippetCreateGQL = new graphql.Graphql(graphqlUrl, "mutation($category: String! $title: String! $body: String!){ snippet ( category: $category title: $title body: $body ){ key }}");
            this.snippetUpdateGQL = new graphql.Graphql(graphqlUrl, "mutation($key: String! $category: String! $title: String! $body: String!){ snippet ( key: $key category: $category title: $title body: $body ){ key }}");
            this.deleteSnippetGQL = new graphql.Graphql(graphqlUrl, "mutation($snippet: String!) { deleteSnippet ( key: $snippet ) }");
            this.deleteCategoryGQL = new graphql.Graphql(graphqlUrl, "mutation($category: String!) { deleteCategory ( key: $category ) }");
            this.searchCategoryGQL = new graphql.Graphql(graphqlUrl, "query($category: String! $match: String!){ snippets( category: $category match: $match ){ key title }}");
            this.datastoreGQL = new graphql.Graphql(graphqlUrl, "{ datastore { categories { key name }  snippets { key category title body } }}");

            // Functions
            this.loadCategories = function () {
                console.log("loadCategories");
                _this.categoryGQL.execute(function (response) {
                    var list = response.data.categories.sort(function (a, b) {
                        return a.name.localeCompare(b.name);
                    });
                    $(_this.categories).empty();
                    $(_this.snippetCategories).empty();
                    $(_this.moveCategories).empty();
                    list.forEach(function (element) {
                        _this.categories.append($("<option></option>").attr("value", element.key).text(element.name));
                        _this.snippetCategories.append($("<option></option>").attr("value", element.key).text(element.name));
                        _this.moveCategories.append($("<option></option>").attr("value", element.key).text(element.name));
                    });

                    window.setTimeout(function () {
                        $(_this.categories).change();
                    }, 1);
                });
            };

            this.loadAllTitles = function () {
                _this.categorySnippetsGQL.variables["category"] = $(_this.categories).val();
                console.log("Snippets for Category");
                _this.categorySnippetsGQL.execute(function (response) { _this.loadTitles(response.data.snippets) });
            };

            this.loadTitles = function (list) {
                console.log("loadTitles");
                $("option", _this.titles).remove();
                $(_this.body).val("");
                list.sort(function (a, b) {
                    return a.title.localeCompare(b.title);
                }).forEach(function (element) {
                    _this.titles.append($("<option></option>").attr("value", element.key).text(element.title))
                });
            };

            this.loadBody = function () {
                _this.snippetBodyGQL.variables["snippet"] = $(_this.titles).val();
                console.log("Requesting snippet");
                _this.snippetBodyGQL.execute(function (response) { $(_this.body).val(response.data.snippet.body); });
            };

            this.saveCategory = function () {
                console.log("Create Category");
                _this.categoryCreateGQL.variables["name"] = $(_this.category).val();
                _this.categoryCreateGQL.execute(function () {
                    _this.loadCategories();
                    $("#category").val("");
                });
            };

            this.saveSnippet = function () {
                console.log("Create Snippet");
                _this.snippetCreateGQL.variables["category"] = $(_this.snippetCategories).val();
                _this.snippetCreateGQL.variables["title"] = $(_this.title).val();
                _this.snippetCreateGQL.variables["body"] = $(_this.bodyInput).val();
                _this.snippetCreateGQL.execute(function () {
                    _this.loadCategories();
                    $(_this.title).val("");
                    $(_this.bodyInput).val("");
                });
            };

            this.deleteSnippet = function () {
                _this.deleteSnippetGQL.variables["snippet"] = $(titles).find("option:selected").val();
                console.log("Delete Snippet");
                _this.deleteSnippetGQL.execute(function () { _this.loadCategories(); });
            };

            this.moveSnippet = function () {
                var selected = $(titles).find("option:selected");
                console.log("Move Snippet: " + $(selected).val());
                _this.snippetUpdateGQL.variables["key"] = selected.val();
                _this.snippetUpdateGQL.variables["category"] = $(_this.moveCategories).val();
                _this.snippetUpdateGQL.variables["title"] = selected.text();
                _this.snippetUpdateGQL.variables["body"] = $(_this.body).val();
                _this.snippetUpdateGQL.execute(function () {
                    _this.loadAllTitles();
                    $(_this.moveSnippetDialog).dialog("close");
                });
            };

            this.deleteCategory = function () {
                _this.deleteCategoryGQL.variables["category"] = $(_this.categories).val();
                console.log("Delete Category");
                _this.deleteCategoryGQL.execute(function () { _this.loadCategories(); });
            };

            this.performSearch = function (event) {
                if (event.keyCode === 13) {
                    _this.searchCategoryGQL.variables["category"] = $(_this.categories).val();
                    _this.searchCategoryGQL.variables["match"] = $(_this.query).val();
                    console.log("Search Category");
                    _this.searchCategoryGQL.execute(function (response) {
                        _this.loadTitles(response.data.snippets);
                        $(_this.searchDialog).dialog("close");
                    });
                }
            };

            this.updateSnippet = function () {
                console.log("Update Snippet");
                _this.snippetUpdateGQL.variables["key"] = $(_this.titles).val();
                _this.snippetUpdateGQL.variables["category"] = $(_this.categories).val();
                _this.snippetUpdateGQL.variables["title"] = $(_this.titles).children(":selected").text();
                _this.snippetUpdateGQL.variables["body"] = $(_this.body).val();
                _this.snippetUpdateGQL.execute(function () {
                    _this.loadAllTitles();
                });
            };

            this.logout = function () {
                console.log("logout");
                $.ajax({
                    url: "v1/auth",
                    method: "DELETE",
                    success: function () { window.location.replace("/login.html"); }
                });
                var auth2 = gapi.auth2.getAuthInstance();
                auth2.signOut().then(function () {
                    console.log("User signed out of Google.");
                });
            };

            this.buildInfo = function () {
                console.log("build info");
                $.get("properties", function (data) {
                    $(_this.buildInfoDialog).dialog("open");
                    $("#buildInfo").html(data);
                });
            };

            this.export = function () {
                var filename = "export.json";
                console.log("export: " + filename);
                _this.datastoreGQL.execute(function (response) {
                    var blob = new Blob([JSON.stringify(response.data.datastore)], { type: 'application/json' });
                    if (window.navigator.msSaveOrOpenBlob) {
                        window.navigator.msSaveBlob(blob, filename);
                    } else {
                        var elem = window.document.createElement('a');
                        elem.href = window.URL.createObjectURL(blob);
                        elem.download = filename;
                        document.body.appendChild(elem);
                        elem.click();
                        document.body.removeChild(elem);
                    }
                });
            };

            this.import = function (evt) {
                var files = evt.target.files;
                for (var i = 0, f; f = files[i]; i++) {
                    console.log("File " + f);
                    var reader = new FileReader();
                    reader.onloadend = function(result) {
                          if (result.target.readyState == FileReader.DONE) {
                            console.log(result.target.result);
                          }
                        };
                     var blob = f.slice(0, f.size);
                     reader.readAsBinaryString(blob);
                }
                $(_this.fileImportDialog).dialog("close");
            };

            this.openSearch = function () {
                $(_this.searchDialog).dialog("open");
            };

            this.openMoveSnippet = function () {
                $(_this.moveSnippetDialog).dialog("open");
            };

            this.moveDialog = function () {
                _this.moveSnippet();
            };

            this.openFileImport = function () {
                $(_this.fileImportDialog).dialog("open");
            };

            // Bindings
            $(this.categories).change(this.loadAllTitles);
            $(this.titles).change(this.loadBody);
            $("#saveSnippetButton").click(this.saveSnippet);
            $("#deleteButton").click(this.deleteSnippet);
            $("#saveCategoryButton").click(this.saveCategory);
            $("#deleteCategoryButton").click(this.deleteCategory);
            $("#searchButton").click(this.openSearch);
            $(this.query).keyup(this.performSearch);
            $("#buildInfoButton").click(this.buildInfo);
            $("#updateButton").click(this.updateSnippet);
            $("#moveButton").click(this.openMoveSnippet);
            $("#logoutButton").click(this.logout);
            $("#performMove").click(this.moveSnippet);
            $("#exportButton").click(this.export);
            $("#fileImportButton").click(this.openFileImport);
            $("#filename").change(this.import);

            // GO!
            this.loadCategories();
            $("#page").show();
            $("#loading").hide();
        }
    }
});

