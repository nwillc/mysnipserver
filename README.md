# MySnipServer

This is a simple snippets app. It is a GraphQL server based on:

- Java 8
- [Ratpack](https://ratpack.io/) servlet providing [GraphQL API](http://graphql.org/)
- JDBC, MongoDB or Memory for persistence (Uses [OPA](https://github.com/nwillc/opa))
- Google authentication
- jQuery UI

## Runtime Configuration

There is a 'env.sh.example' file that exports variables used by the provided 'run.sh'. Look at this for explanations of the runtime configuration.

## Docker Tasks

Can create an image, or run in a container, right from the gradle build.

## See Also

Posts on this service:

- [First Project wih RatPack](https://nwillc.wordpress.com/2017/04/30/first-project-with-ratpack/)
- [Dropbox to Orchestrate](https://nwillc.wordpress.com/2015/10/30/from-dropbox-to-orchestrate/)
- [GraphQL](https://nwillc.wordpress.com/2016/10/13/graphql-java-server-javascript-client/)
- [Orchestrate to Mongo](https://nwillc.wordpress.com/2016/11/19/orchestrate-to-mongodb/)

-----
[![Coverage](https://codecov.io/gh/nwillc/mysnipserver/branch/master/graphs/badge.svg?branch=master)](https://codecov.io/gh/nwillc/mysnipserver)
[![license](https://img.shields.io/github/license/nwillc/mysnipserver.svg)](https://tldrlegal.com/license/-isc-license)
[![Travis](https://img.shields.io/travis/nwillc/mysnipserver.svg)](https://travis-ci.org/nwillc/mysnipserver)
