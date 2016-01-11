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

APP.Home = function () {
    // Instance variables and initialization
    $('#tabs').tabs();
    this.categories = $('#browseCategories');
    this.snippetCategories = $('#snippetCategories');
    this.category = $('#category');
    this.titles = $('#titles');
    this.title = $('#title');
    this.body = $('#body');
    this.bodyInput = $('#bodyInput');
    this.query = $('#query');
    this.searchDialog = $('#searchCategoryDialog');
    $(this.searchDialog).dialog({width: 500});
    $(this.searchDialog).dialog('close');
    this.buildInfoDialog = $('#buildInfoDialog');
    $(this.buildInfoDialog).dialog({height: 200, width: 500});
    $(this.buildInfoDialog).dialog('close');

    // Functions
    this.loadCategories = function () {
        var that = this;
        console.log("loadCategories");
        $.get("v1/categories", function (data) {
            var list = JSON.parse(data);
            $(that.categories).empty();
            list.sort(function (a, b) {
                return a.name.toLowerCase() > b.name.toLowerCase();
            }).forEach(function (element) {
                that.categories.append($("<option></option>").attr("value", element.key).text(element.name));
            });
            window.setTimeout(function () {
                $(that.categories).change();
            }, 1);
            $(that.snippetCategories).empty();
            list.sort(function (a, b) {
                return a.name.toLowerCase() > b.name.toLowerCase();
            }).forEach(function (element) {
                that.snippetCategories.append($("<option></option>").attr("value", element.key).text(element.name));
            });
        });
    };

    this.loadAllTitles = function () {
        var that = this;
        console.log("loadAllTitles");
        var category = $(this.categories).val();
        console.log("Selected Category: " + category);
        $.get("v1/snippets/category/" + category, function (data) {
            that.loadTitles(JSON.parse(data));
        })
    };

    this.loadTitles = function (list) {
        var that = this;
        console.log("loadTitles");
        $('option', this.titles).remove();
        $(this.body).val('');
        list.sort(function (a, b) {
            return a.title.toLowerCase() > b.title.toLowerCase();
        }).forEach(function (element) {
            that.titles.append($("<option></option>").attr("value", element.key).text(element.title));
        })
    };

    this.loadBody = function () {
        var that = this;
        console.log("loadBody");
        var category = $(this.categories).val();
        var title = $(this.titles).val();
        console.log("Selected Category: " + category + " Title: " + title);
        $.get("v1/snippets/" + title, function (data, status) {
            console.log("Status: " + status + " Data: " + data);
            var found = JSON.parse(data);
            $(that.body).val(found.body);
        })
    };

    this.saveCategory = function () {
        var that = this;
        console.log("saveCategory");
        $.post("v1/categories", JSON.stringify({
            name: $(this.category).val()
        }), function () {
            that.loadCategories();
            $('#category').val('');
        }).fail(function () {
            alert("Failed saving category");
        });
    };

    this.saveSnippet = function () {
        var that = this;
        console.log("Save Snippet");
        $.post('v1/snippets', JSON.stringify({
            category: $(this.snippetCategories).val(),
            title: $(this.title).val(),
            body: $(this.bodyInput).val()
        }), function () {
            that.loadCategories();
        }).fail(function () {
            alert("Failed saving snippet.")
        });
        $(this.title).val('');
        $(this.bodyInput).val('');
    };

    this.deleteSnippet = function () {
        var that = this;
        var selected = $(titles).find("option:selected");
        console.log("Delete Snippet: " + $(this.categories).val() + ':'
            + $(selected).val());
        $.ajax({
            url: 'v1/snippets/' + $(selected).val(),
            type: 'DELETE',
            success: function () {
                console.log('success');
                that.loadCategories();
            }
        });
    };

    this.deleteCategory = function () {
        var that = this;
        console.log("Delete Category: " + $(this.categories).val());
        $.ajax({
            url: 'v1/categories/' + $(this.categories).val(),
            type: 'DELETE',
            success: function () {
                console.log('success');
                that.loadCategories();
            }
        });
    };

    this.openSearch = function () {
        $(this.searchDialog).dialog('open');
    };

    this.performSearch = function () {
        var that = this;
        console.log("loadAllTitles");
        var category = $(this.categories).val();
        console.log("Selected Category: " + category);
        $.post("v1/snippets/category/" + category, JSON.stringify({
            query: $(query).val()
        }), function (data) {
            that.loadTitles(JSON.parse(data));
            $(that.searchDialog).dialog('close');
        }).fail(function () {
            alert("Failed searching snippets.")
        });
    };

    this.logout = function () {
        console.log("logout");
        APP.myPersona.logout();
        $.ajax({
            url: 'v1/auth',
            type: 'DELETE',
            success: function () {
                window.location.replace("/login.html");
            }
        });
    };

    this.buildInfo = function () {
        var that = this;
        console.log("build info");
        $.get("properties", function (data) {
            $(that.buildInfoDialog).dialog('open');
            $('#buildInfo').html(data);
        });
    };

    // Bindings
    $(this.categories).change(this.loadAllTitles.bind(this));
    $(this.titles).change(this.loadBody.bind(this));
    $('#saveSnippetButton').click(this.saveSnippet.bind(this));
    $('#logoutButton').click(this.logout.bind(this));
    $('#deleteButton').click(this.deleteSnippet.bind(this));
    $('#saveCategoryButton').click(this.saveCategory.bind(this));
    $('#deleteCategoryButton').click(this.deleteCategory.bind(this));
    $('#searchButton').click(this.openSearch.bind(this));
    $('#performSearch').click(this.performSearch.bind(this));
    $('#buildInfoButton').click(this.buildInfo.bind(this));

    // Go!
    this.loadCategories();
};

$(document).ready(function () {
    new APP.Home();
});
