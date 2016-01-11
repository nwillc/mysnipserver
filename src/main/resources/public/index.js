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

var APP = APP || {};

APP.home = {

    init: function (config) {
        console.log("init");
        APP.home.config = {
            categories: $('#browseCategories'),
            snippetCategories: $('#snippetCategories'),
            category: $('#category'),
            titles: $('#titles'),
            title: $('#title'),
            body: $('#body'),
            bodyInput: $('#bodyInput'),
            searchDialog: $('#searchCategoryDialog'),
            query: $('#query'),
            buildInfoDialog: $('#buildInfoDialog')
        };
        $.extend(APP.home.config, config);
        APP.home.bind();
        APP.home.loadCategories();
    },

    bind: function () {
        console.log("bind");
        $('#tabs').tabs();
        $(APP.home.config.searchDialog).dialog({ width: 500 });
        $(APP.home.config.searchDialog).dialog('close');
        $(APP.home.config.buildInfoDialog).dialog({ height: 200, width: 500 });
        $(APP.home.config.buildInfoDialog).dialog('close');
        $(APP.home.config.categories).change(APP.home.loadAllTitles);
        $(APP.home.config.titles).change(APP.home.loadBody);
        $('#saveSnippetButton').click(APP.home.saveSnippet);
        $('#logoutButton').click(APP.home.logout);
        $('#deleteButton').click(APP.home.deleteSnippet);
        $('#saveCategoryButton').click(APP.home.saveCategory);
        $('#deleteCategoryButton').click(APP.home.deleteCategory);
        $('#searchButton').click(APP.home.openSearch);
        $('#performSearch').click(APP.home.performSearch);
        $('#buildInfoButton').click(APP.home.buildInfo);
    },

    loadCategories: function () {
        console.log("loadCategories");
        $.get("v1/categories", function (data) {
            var list = JSON.parse(data);
            $(APP.home.config.categories).empty();
            $(list).sort(function (a, b) {
                return a.name.toLowerCase() > b.name.toLowerCase();
            }).each(function () {
                APP.home.config.categories.append($("<option></option>").attr("value", this.key).text(this.name));
            });
            window.setTimeout(function () {
                $(APP.home.config.categories).change();
            }, 1);
            $(APP.home.config.snippetCategories).empty();
            $(list).sort(function (a, b) {
                return a.name.toLowerCase() > b.name.toLowerCase();
            }).each(function () {
                APP.home.config.snippetCategories.append($("<option></option>").attr("value", this.key).text(this.name));
            });
        });
    },

    loadAllTitles: function () {
        console.log("loadAllTitles");
        var category = $(APP.home.config.categories).val();
        console.log("Selected Category: " + category);
        $.get("v1/snippets/category/" + category, function (data) {
            APP.home.loadTitles(JSON.parse(data));
        })
    },

    loadTitles: function (list) {
        console.log("loadTitles");
        $('option', APP.home.config.titles).remove();
        $(APP.home.config.body).val('');
        $(list).sort(function (a, b) {
            return a.title.toLowerCase() > b.title.toLowerCase();
        }).each(function () {
            APP.home.config.titles.append($("<option></option>").attr("value", this.key).text(this.title));
        })
    },

    loadBody: function () {
        console.log("loadBody");
        var category = $(APP.home.config.categories).val();
        var title = $(APP.home.config.titles).val();
        console.log("Selected Category: " + category + " Title: " + title);
        $.get("v1/snippets/" + title, function (data, status) {
            console.log("Status: " + status + " Data: " + data);
            var found = JSON.parse(data);
            $(APP.home.config.body).val(found.body);
        })
    },

    saveCategory: function () {
        console.log("saveCategory");
        $.post("v1/categories", JSON.stringify({
            name: $(APP.home.config.category).val()
        }), function () {
            APP.home.loadCategories();
            $('#category').val('');
        }).fail(function () {
            alert("Failed saving category");
        });
    },

    saveSnippet: function () {
        console.log("Save Snippet");
        $.post('v1/snippets', JSON.stringify({
            category: $(APP.home.config.snippetCategories).val(),
            title: $(APP.home.config.title).val(),
            body: $(APP.home.config.bodyInput).val()
        }), function () {
            APP.home.loadCategories();
        }).fail(function () {
            alert("Failed saving snippet.")
        });
        $(APP.home.config.title).val('');
        $(APP.home.config.bodyInput).val('');
    },

    deleteSnippet: function () {
        var selected = $(titles).find("option:selected");
        console.log("Delete Snippet: " + $(APP.home.config.categories).val() + ':'
            + $(selected).val());
        $.ajax({
            url: 'v1/snippets/' + $(selected).val(),
            type: 'DELETE',
            success: function () {
                console.log('success');
                APP.home.loadCategories();
            }
        });
    },

    deleteCategory: function () {
        console.log("Delete Category: " + $(APP.home.config.categories).val());
        $.ajax({
            url: 'v1/categories/' + $(APP.home.config.categories).val(),
            type: 'DELETE',
            success: function () {
                console.log('success');
                APP.home.loadCategories();
            }
        });
    },

    openSearch: function () {
        $(APP.home.config.searchDialog).dialog('open');
    },

    performSearch: function () {
        console.log("loadAllTitles");
        var category = $(APP.home.config.categories).val();
        console.log("Selected Category: " + category);
        $.post("v1/snippets/category/" + category, JSON.stringify({
            query: $(query).val()
        }), function (data) {
            APP.home.loadTitles(JSON.parse(data));
            $(APP.home.config.searchDialog).dialog('close');
        }).fail(function () {
            alert("Failed searching snippets.")
        });
    },

    logout: function () {
        console.log("logout");
        APP.myPersona.logout();
        $.ajax({
            url: 'v1/auth',
            type: 'DELETE',
            success: function () {
                window.location.replace("/login.html");
            }
        });
    },

    buildInfo: function () {
        console.log("build info");
        $.get("properties", function (data) {
            $(APP.home.config.buildInfoDialog).dialog('open');
            $('#buildInfo').html(data);
        });
    }

};

$(document).ready(function () {
    APP.home.init();
});
