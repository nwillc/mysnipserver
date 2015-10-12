  $(document).ready(function(){
            $("button").click(function(){
                $.get("v1/snippets/category/Java", function(data, status){
                    var list = JSON.parse(data);
                    var categories = $("#categories");
                    $(list).each(function(){
                       categories.append(new Option(this,this));
                    });
                });
            });
        });
