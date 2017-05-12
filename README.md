# MySnipServer

This is a simple snippets app. It is a GraphQL server based on:

- Java 8
- [Ratpack](https://ratpack.io/) servlet providing [GraphQL API](http://graphql.org/)
- MongoDB for persistence ([mlab](https://mlab.com))
- Google authentication
- jQuery UI

I use [Travis-CI](https://travis-ci.org/) for my Continous Integration and Deployment.

## Runtime Configuration

There is a 'env.sh.example' file that exports variables used by the provided 'run.sh'. Look at this for explanations of
the runtime configuration.

## See Also

Posts on this service:

- [First Project wih RatPack](https://nwillc.wordpress.com/2017/04/30/first-project-with-ratpack/)
- [Dropbox to Orchestrate](https://nwillc.wordpress.com/2015/10/30/from-dropbox-to-orchestrate/)
- [GraphQL](https://nwillc.wordpress.com/2016/10/13/graphql-java-server-javascript-client/)
- [Orchestrate to Mongo](https://nwillc.wordpress.com/2016/11/19/orchestrate-to-mongodb/)

-----
[![ISC License](http://shields-nwillc.rhcloud.com/shield/tldrlegal?package=ISC)](http://shields-nwillc.rhcloud.com/homepage/tldrlegal?package=ISC)
[![Build Status](http://shields-nwillc.rhcloud.com/shield/travis-ci?path=nwillc&package=mysnipserver)](http://shields-nwillc.rhcloud.com/homepage/travis-ci?path=nwillc&package=mysnipserver)
