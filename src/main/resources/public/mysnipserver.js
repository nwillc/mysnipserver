/*
 * Copyright (c) 2015,  nwillc@gmail.com
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

var myPresentation = {

    init: function (config) {
        console.log("init");
        myPresentation.config = {
            categories: $('#browseCategories'),
            snippetCategories: $('#snippetCategories'),
            category: $('#category'),
            titles: $('#titles'),
            title: $('#title'),
            body: $('#body'),
            bodyInput: $('#bodyInput')
        };
        $.extend(myPresentation.config, config);
        myPresentation.bind();
        myPresentation.loadCategories();
    },

    bind: function () {
        console.log("bind");
        $('#tabs').tabs();
        $(myPresentation.config.categories).change(myPresentation.loadTitles);
        $(myPresentation.config.titles).change(myPresentation.loadBody);
        $('#saveSnippetButton').click(myPresentation.saveSnippet);
        $('#logoutButton').click(myPresentation.logout);
        $('#deleteButton').click(myPresentation.deleteSnippet);
        $('#saveCategoryButton').click(myPresentation.saveCategory);
        $('#deleteCategoryButton').click(myPresentation.deleteCategory);
    },

    loadCategories: function () {
        console.log("loadCategories");
        $.get("v1/categories", function (data) {
            var list = JSON.parse(data);
            $(myPresentation.config.categories).empty();
            $(list).sort(function (a, b) {
                return a.name > b.name;
            }).each(function () {
                myPresentation.config.categories.append(new Option(this.name, this.key));
            });
            window.setTimeout(function () {
                $(myPresentation.config.categories).change();
            }, 1);
            $(myPresentation.config.snippetCategories).empty();
            $(list).sort(function (a, b) {
                return a.name > b.name;
            }).each(function () {
                myPresentation.config.snippetCategories.append(new Option(this.name, this.key));
            });
        });
    },

    loadTitles: function () {
        console.log("loadTitles");
        var category = $(myPresentation.config.categories).val();
        console.log("Selected Category: " + category);
        $('option', myPresentation.config.titles).remove();
        $(myPresentation.config.body).val('');
        $.get("v1/snippets/category/" + category, function (data) {
            var list = JSON.parse(data);
            $(list).each(function () {
                myPresentation.config.titles.append(new Option(this.title, this.key));
            })
        })
    },

    loadBody: function () {
        console.log("loadBody");
        var category = $(myPresentation.config.categories).val();
        var title = $(myPresentation.config.titles).val();
        console.log("Selected Category: " + category + " Title: " + title);
        $.get("v1/snippets/" + title, function (data, status) {
            console.log("Status: " + status + " Data: " + data);
            var found = JSON.parse(data);
            $(myPresentation.config.body).val(found.body);
        })
    },

    saveCategory: function () {
        console.log("saveCategory");
        $.post("v1/categories", JSON.stringify({
            name: $(myPresentation.config.category).val()
        }), function () {
            myPresentation.loadCategories();
            $('#category').val('');
        }).fail(function () {
            alert("Failed saving category");
        });
    },

    saveSnippet: function () {
        console.log("Save Snippet");
        $.post('v1/snippets', JSON.stringify({
            category: $(myPresentation.config.snippetCategories).val(),
            title: $(myPresentation.config.title).val(),
            body: $(myPresentation.config.bodyInput).val()
        }), function () {
            myPresentation.loadCategories();
        }).fail(function () {
            alert("Failed saving snippet.")
        });
        $(myPresentation.config.title).val('');
        $(myPresentation.config.bodyInput).val('');
    },

    deleteSnippet: function () {
        console.log("Delete Snippet: " + $(myPresentation.config.categories).val() + ':'
            + $("#titles option:selected").val());
        $.ajax({
            url: 'v1/snippets/' + $("#titles option:selected").val(),
            type: 'DELETE',
            success: function () {
                console.log('success');
                myPresentation.loadCategories();
            }
        });
    },

    deleteCategory: function () {
        console.log("Delete Category: " + $(myPresentation.config.categories).val());
        $.ajax({
            url: 'v1/categories/' + $(myPresentation.config.categories).val(),
            type: 'DELETE',
            success: function () {
                console.log('success');
                myPresentation.loadCategories();
            }
        });
    },

    logout: function () {
        console.log("logout");
        $.ajax({
            url: 'v1/auth',
            type: 'DELETE',
            success: function () {
                window.location.replace("/login.html");
            }
        });
    }

};

$(document).ready(function () {
    myPresentation.init();
});
