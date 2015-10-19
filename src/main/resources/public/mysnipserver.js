function LoadCategories() {
    var categories = $("#categories");
    $.get("v1/categories", function (data, status) {
        var list = JSON.parse(data);
        $(categories).empty();
        $(list)
            .sort(function (a, b) {
                return a.name > b.name;
            })
            .each(function () {
                categories.append(new Option(this.name, this.name));
            });
    });
}

$(document).ready(function () {
    var ping = $("#ping");
    var categories = $("#categories");
    var titles = $("#titles");
    var body = $("#body");

    $("div").hide();

    $("#browseButton").click(function () {
        $("div").hide();
        LoadCategories();
        $("#categoryDiv").show();
        $("#browseDiv").show();
    });

    $("#newSnippetButton").click(function () {
        $("div").hide();
        LoadCategories();
        $("#categoryDiv").show();
        $("#newSnippetDiv").show();
    });

    $("#newCategoryButton").click(function () {
        $("div").hide();
        $("#newCategoryDiv").show();
    });

    $(categories).change(function () {
        var category = $(categories).val();
        console.log("Selected Category: " + category);
        $('option', '#titles').remove();
        $(body).val('');
        $.get("v1/snippets/category/" + category, function (data, status) {
            var list = JSON.parse(data);
            $(list).each(function () {
                titles.append(new Option(this.title, this.title));
            })
        })
    });

    $(titles).change(function () {
        var category = $(categories).val();
        var title = $(titles).val();
        console.log("Selected Category: " + category + " Title: " + title);
        $.get("v1/snippets/category/" + category + "/title/" + title, function (data, status) {
            console.log("Status: " + status + " Data: " + data);
            var found = JSON.parse(data);
            $(body).val(found.body);
        })
    });

    $("#category").change(function () {
        $.post("v1/categories", JSON.stringify({name: $("#category").val()}));
        $('#category').val('');
    });

    $('#bodyInput').change(function () {
        $.post('v1/snippets', JSON.stringify({category: 'Java', title: 'default method', body: 'This is the body'}));
    });
});
