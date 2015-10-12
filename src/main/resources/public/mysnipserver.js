 $(document).ready(function(){
    var ping = $("#ping");
    var categories = $("#categories");
    var titles = $("#titles");

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

    $.get("v1/categories", function(data, status){
        var list = JSON.parse(data);
        $(list).each(function(){
            categories.append(new Option(this,this));
        });
    });

 });
