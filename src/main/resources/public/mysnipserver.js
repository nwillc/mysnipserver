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
            categories: $('#categories'),
            category: $('#category'),
            titles: $('#titles'),
            title: $('#title'),
            body: $('#body'),
            bodyInput: $('#bodyInput')
        };
        $.extend(myPresentation.config, config);
        myPresentation.bind();
    },

    bind: function () {
        console.log("bind");
        myPresentation.hideAll();
        $('#browseButton').click(myPresentation.showBrowse);
        $('#newSnippetButton').click(myPresentation.showNewSnippet);
        $('#newCategoryButton').click(myPresentation.showNewCategory);
        $('#saveSnippetButton').click(myPresentation.saveSnippet);
        $('#logoutButton').click(myPresentation.logout);
        $(myPresentation.config.categories).change(myPresentation.loadTitles);
        $(myPresentation.config.titles).change(myPresentation.loadBody);
        $(myPresentation.config.category).change(myPresentation.saveCategory);
    },

    hideAll: function () {
        $('div').hide();
    },

    showBrowse: function () {
        console.log('showBrowse');
        myPresentation.hideAll();
        myPresentation.loadCategories();
        $("#categoryDiv").show();
        $("#browseDiv").show();
        $(myPresentation.config.categories).focus();
    },

    showNewSnippet: function () {
        console.log("showNewSnippet");
        myPresentation.hideAll();
        myPresentation.loadCategories();
        $("#categoryDiv").show();
        $("#newSnippetDiv").show();
        $(myPresentation.config.categories).focus();
    },

    showNewCategory: function () {
        console.log("showNewCategory");
        myPresentation.hideAll();
        $("#newCategoryDiv").show();
        $(myPresentation.config.category).focus();
    },

    loadCategories: function () {
        console.log("loadCategories");
        $.get("v1/categories", function (data, status) {
            var list = JSON.parse(data);
            $(myPresentation.config.categories).empty();
            $(list)
                .sort(function (a, b) {
                    return a.name > b.name;
                })
                .each(function () {
                    myPresentation.config.categories.append(new Option(this.name, this.name));
                });
            $("#categories :nth(0)").prop("selected", "selected").change()
        });
    },

    loadTitles: function () {
        console.log("loadTitles");
        var category = $(myPresentation.config.categories).val();
        console.log("Selected Category: " + category);
        $('option', myPresentation.config.titles).remove();
        $(myPresentation.config.body).val('');
        $.get("v1/snippets/category/" + category, function (data, status) {
            var list = JSON.parse(data);
            $(list).each(function () {
                myPresentation.config.titles.append(new Option(this.title, this.title));
            })
        })
    },

    loadBody: function () {
        console.log("loadBody");
        var category = $(myPresentation.config.categories).val();
        var title = $(myPresentation.config.titles).val();
        console.log("Selected Category: " + category + " Title: " + title);
        $.get("v1/snippets/category/" + category + "/title/" + title, function (data, status) {
            console.log("Status: " + status + " Data: " + data);
            var found = JSON.parse(data);
            $(myPresentation.config.body).val(found.body);
        })
    },

    saveCategory: function () {
        console.log("saveCategory");
        $.post("v1/categories", JSON.stringify({
            name: $(myPresentation.config.category).val()
        }))
            .fail(function () {
                alert("Failed saving category");
            });
        $('#category').val('');
    },

    saveSnippet: function () {
        console.log("Save Snippet");
        $.post('v1/snippets', JSON.stringify({
            category: $(myPresentation.config.categories).val(),
            title: $(myPresentation.config.title).val(),
            body: $(myPresentation.config.bodyInput).val()
        }))
            .fail(function () {
                alert("Failed saving snippet.")
            });
        $(myPresentation.config.title).val('');
        $(myPresentation.config.bodyInput).val('');
    },

    logout: function () {
        console.log("logout");
        $.ajax({
            url: 'v1/auth',
            type: 'DELETE',
            success: function () {
                window.location.replace("/login.html");
            }});
    }

};

$(document).ready(function () {
    myPresentation.init();
});
