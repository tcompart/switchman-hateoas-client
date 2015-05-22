# switchman-hateoas-client
[![Build Status](https://api.travis-ci.org/ImmobilienScout24/switchman-hateoas-client.svg?branch=master)](https://travis-ci.org/ImmobilienScout24/switchman-hateoas-client)
[![Coverage Status](https://coveralls.io/repos/ImmobilienScout24/switchman-hateoas-client/badge.svg)](https://coveralls.io/r/ImmobilienScout24/switchman-hateoas-client)

Utility classes for HATEOAS clients using Hystrix and Spring Cache Abstraction.

## What is the use of this?

If you use a HATEOAS service, your client implementation should "surf" the API until it finds the resource you want to access.
This is cool, because due to this the server can change all urls but the base url known to the clients.

But there is also a problem with this - performance. If you browse the API over and over you produce a lot of unnecessary
requests. 

### Why not cache the way to a resource?

This is what this library does - using Spring cache abstraction.

### What else?

This library also combines the caching with the great Hystrix library by Netflix. You may use a HateoasRemoteCommand to
make use of this caching.

### How to use it in my project?

Add the library to your pom.xml:

```xml
<dependency>
  <groupId>de.is24.common</groupId>
  <artifactId>switchman-hateoas-client</artifactId>
  <version>1.0</version>
</dependency>
```

Then, you can extend HateoasRemoteCommand:

```java
  public class MyRemoteCommand extends HateoasRemoteCommand<Integer> {        
    private static final String RELATION = "myRelation";
    private static final String COMMAND_GROUP_KEY = "MyCommandGroup";
    
    public MyRemoteCommand(RestOperations restOperations,
                                       HateoasLinkProvider hateoasLinkProvider) {
      super(new HystrixConfiguration(true, 100000).getConfiguration(COMMAND_GROUP_KEY), 
            restOperations, 
            hateoasLinkProvider);
    }
    @Override
    protected Integer runCommand() throws Exception {
      Link linkToConfigurations = getLinkByName(baseUrlOfTheRemoteApi, RELATION).expand();
      return restOperations.getForEntity(linkToConfigurations.getHref(), Integer.class);
    }
  }
```
