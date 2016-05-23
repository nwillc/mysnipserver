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

function gapiAuth2Load () {
    "use strict";
    console.log("Loading gapi auth2");
    gapi.load('auth2', function() {
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
    this.category = $('#category');
    this.titles = $('#titles');
    this.title = $('#title');
    this.body = $('#body');
    this.bodyInput = $('#bodyInput');
    this.query = $('#query');

    this.searchDialog = $('#searchCategoryDialog');
    $(this.searchDialog).dialog({width: 500});
    $(this.searchDialog).dialog('close');

    this.moveSnippetDialog = $('#moveSnippetDialog');
    $(this.moveSnippetDialog).dialog({width:200});
    $(this.moveSnippetDialog).dialog('close');

    this.buildInfoDialog = $('#buildInfoDialog');
    $(this.buildInfoDialog).dialog({height: 200, width: 500});
    $(this.buildInfoDialog).dialog('close');

    // Functions
    this.loadCategories = () => {
        console.log("loadCategories");
        $.get("v1/categories", data => {
            var list = JSON.parse(data).sort((a, b) => {
                return a.name.localeCompare(b.name);
            });
            $(this.categories).empty();
            list.forEach(element =>
                this.categories.append($("<option></option>").attr("value", element.key).text(element.name)));
            window.setTimeout(() => {
                $(this.categories).change();
            }, 1);
            $(this.snippetCategories).empty();
            list.forEach(element =>
                this.snippetCategories.append($("<option></option>").attr("value", element.key).text(element.name)));
        });
    };

    this.loadAllTitles = () => {
        console.log("loadAllTitles");
        var category = $(this.categories).val();
        console.log("Selected Category: " + category);
        $.get("v1/snippets/category/" + category, data => this.loadTitles(JSON.parse(data)));
    };

    this.loadTitles = (list) => {
        console.log("loadTitles");
        $('option', this.titles).remove();
        $(this.body).val('');
        list.sort((a, b) => a.title.localeCompare(b.title)).forEach(element =>
            this.titles.append($("<option></option>").attr("value", element.key).text(element.title)));
    };

    this.loadBody = () => {
        console.log("loadBody");
        var category = $(this.categories).val();
        var title = $(this.titles).val();
        console.log("Selected Category: " + category + " Title: " + title);
        $.get("v1/snippets/" + title, (data, status) => {
            console.log("Status: " + status + " Data: " + data);
            var snippet = JSON.parse(data);
            $(this.body).val(snippet.body);
        })
    };

    this.saveCategory = () => {
        console.log("saveCategory");
        $.post("v1/categories", JSON.stringify({
            name: $(this.category).val()
        }), () => {
            this.loadCategories();
            $('#category').val('');
        }).fail(() => alert("Failed saving category"));
    };

    this.saveSnippet = () => {
        console.log("Save Snippet");
        $.post('v1/snippets', JSON.stringify({
            category: $(this.snippetCategories).val(),
            title: $(this.title).val(),
            body: $(this.bodyInput).val()
        }), () => this.loadCategories()).fail(() => alert("Failed saving snippet."));
        $(this.title).val('');
        $(this.bodyInput).val('');
    };

    this.deleteSnippet = () => {
        var selected = $(titles).find("option:selected");
        console.log("Delete Snippet: " + $(this.categories).val() + ':'
            + $(selected).val());
        $.ajax({
            url: 'v1/snippets/' + $(selected).val(),
            method: 'DELETE',
            success: () => this.loadCategories()
        });
    };

    this.moveSnippet = () => {
       var selected = $(titles).find("option:selected");
        console.log("Move Snippet: " + $(selected).val());
        $.ajax({
         url: 'v1/snippets/' + $(selected).val() + '/move/' + $(this.categories).val(),
         method: 'PUT',
         success: () => this.loadCategories()
        });
    };

    this.deleteCategory = () => {
        console.log("Delete Category: " + $(this.categories).val());
        $.ajax({
            url: 'v1/categories/' + $(this.categories).val(),
            method: 'DELETE',
            success: () => this.loadCategories()
        });
    };

    this.openSearch = () => {
        $(this.searchDialog).dialog('open');
    };

    this.openMoveSnippet = () => {
        $(this.moveSnippetDialog).dialog('open');
    };

    this.performSearch = () => {
        console.log("loadAllTitles");
        var category = $(this.categories).val();
        console.log("Selected Category: " + category);
        $.post("v1/snippets/category/" + category, JSON.stringify({
            query: $(query).val()
        }), data => {
            this.loadTitles(JSON.parse(data));
            $(this.searchDialog).dialog('close');
        }).fail(() => alert("Failed searching snippets."));
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

    this.updateSnippet = () => {
         console.log("Update Snippet");
         $.post('v1/snippets', JSON.stringify({
             key: $(this.titles).val(),
             category: $(this.categories).val(),
             title: $(this.titles).children(":selected").text(),
             body: $(this.body).val()
          })).fail(() => alert("Failed updating snippet."));
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

    // Go!
    this.loadCategories();
};

$(document).ready(() => {
    APP.home = new APP.Home();
});
