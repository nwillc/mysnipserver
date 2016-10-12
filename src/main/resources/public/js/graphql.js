
var APP = APP || {};

APP.Graphql = function(url, query) {
  "use strict";

  this.url = url;
  this.query = query;
  this.variables = {};

  this.toString = () => {
    return JSON.stringify(this);
  };

  this.execute = (consumer) => {
     console.log("Executing: " + this.toString());
     $.post(this.url,
        this.toString(),
        response => consumer(response)
     );
  };
}