 $(document).ready(function(){
    var ping = $("#ping");
    var categories = $("#categories");
    var titles = $("#titles");
    var body = $("#body");

    $("div").hide();

    $("#browseButton").click(function(){
        $("div").hide();
        $("#categoryDiv").show();
        $("#browseDiv").show();
    });

    $("#newSnippetButton").click(function(){
        $("div").hide();
        $("#categoryDiv").show();
        $("#newSnippetDiv").show();
    });

    $("#newCategoryButton").click(function(){
        $("div").hide();
        $("#newCategoryDiv").show();
    });

    $(categories).change(function() {
        var category = $(categories).val();
        console.log("Selected Category: " + category);
        $.get("v1/snippets/category/" + category, function(data, status){
            var list = JSON.parse(data);
            $(list).each(function(){
                titles.append(new Option(this,this));
            })
        })
    });

    $(titles).change(function(){
        var category = $(categories).val();
        var title = $(titles).val();
        console.log("Selected Category: " + category + " Title: " + title);
        $.get("v1/snippets/category/" + category + "/title/" + title, function(data, status){
            console.log("Status: " + status + " Data: " + data);
            var found = JSON.parse(data);
            $(body).val(found.body);
        })
    });

    $.get("v1/categories", function(data, status){
        var list = JSON.parse(data);
        $(list).each(function(){
            categories.append(new Option(this,this));
        });
    });

 });
