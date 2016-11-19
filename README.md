# MySnipServer

This is a simple snippets app. It is a GraphQL server based on:

- Java 8
- [Java Spark](http://sparkjava.com/) servlet providing [GraphQL API](http://graphql.org/)
- MongoDB for persistence ([mlab](https://mlab.com))
- JSR 107 Caching ([Caffeine](https://github.com/ben-manes/caffeine))   
- Google authentication
- jQuery UI

It gets deployed, when tests pass, by Travis-ci to OpenShift.

Som posts about this service:
 - [Dropbox to Orchestrate](https://nwillc.wordpress.com/2015/10/30/from-dropbox-to-orchestrate/)
 - [GraphQL](https://nwillc.wordpress.com/2016/10/13/graphql-java-server-javascript-client/)
 - [Orchestrate to Mongo](https://nwillc.wordpress.com/2016/11/19/orchestrate-to-mongodb/)

-----
[![ISC License](http://shields-nwillc.rhcloud.com/shield/tldrlegal?package=ISC)](http://shields-nwillc.rhcloud.com/homepage/tldrlegal?package=ISC)
[![Build Status](http://shields-nwillc.rhcloud.com/shield/travis-ci?path=nwillc&package=mysnipserver)](http://shields-nwillc.rhcloud.com/homepage/travis-ci?path=nwillc&package=mysnipserver)
