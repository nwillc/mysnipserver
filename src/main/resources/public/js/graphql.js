
var APP = APP || {};

APP.Graphql = function(query) {
  "use strict";

  this.query = query;
  this.variables = {};
}