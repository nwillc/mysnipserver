 $(document).ready(function(){
    var ping = $("#ping");
    var categories = $("#categories");
    var titles = $("#titles");
    var snippet = $("#snippet");

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
        $.get("v1/snippet/category/" + category + "/title/" + title, function(data, status){
            console.log("Status: " + status + " Data: " + data);
            var found = JSON.parse(data);
            $(snippet).val(found.snippet);
        })
    });

    $.get("v1/categories", function(data, status){
        var list = JSON.parse(data);
        $(list).each(function(){
            categories.append(new Option(this,this));
        });
    });

 });
