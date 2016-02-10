HyperExpress Plugin for RestExpress
===================================

The HyperExpress plugin provides a postprocess that automatically converts the models returned
from your controllers into Resource instances, stitching in links, then rendering them as
the requested media type, depending on the Accept header.

It also provides a way to 'expand' links or otherwise augment the outbound Resource before
it gets serialized, via a callback interface.

It is possible to set flags and parameters on the plugin, so that preprocessors and postprocessors
handle them correctly.  For example, making the /api-docs route public so that it doesn't
require authentication or authorization.

Maven Usage
===========
Stable:
```xml
		<dependency>
			<groupId>com.strategicgains</groupId>
			<artifactId>HyperExpressPlugin</artifactId>
            <version>2.6</version>
		</dependency>
```
Development:
```xml
		<dependency>
			<groupId>com.strategicgains</groupId>
			<artifactId>HyperExpressPlugin</artifactId>
            <version>2.7-SNAPSHOT</version>
		</dependency>
```
Or download the jar directly from: 
http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22HyperExpressPlugin%22

Note that to use the SNAPSHOT version, you must enable snapshots and a repository in your pom (or settings.xml) file as follows:
```xml
  <profiles>
    <profile>
       <id>allow-snapshots</id>
          <activation><activeByDefault>true</activeByDefault></activation>
       <repositories>
         <repository>
           <id>snapshots-repo</id>
           <url>https://oss.sonatype.org/content/repositories/snapshots</url>
           <releases><enabled>false</enabled></releases>
           <snapshots><enabled>true</enabled></snapshots>
         </repository>
       </repositories>
     </profile>
  </profiles>
```

Usage
=====

Simply instantiate the plugin and register it with the RestExpress server, setting options
as necessary, using method chaining if desired.

For example:
```java
RestExpress server = new RestExpress()...

// Creation of a ResourceFactoryStrategy is optional, but allows
// you to customize the functionality of the factory by informing it
// about model annotations that indicate which properties to include
// and/or exclude.
ResourceFactoryStrategy halFactory = new HalResourceFactory()
	.includeAnnotations(JsonProperty.class)						// optional. properties annotated with this are included in outbound resources.
	.excludeAnnotations(JsonIgnore.class)						// optional. properties annotated with this are not included in outbound resources.

new HyperExpressPlugin(Linkable.class)							// optional to pass in the marker interface that denotes the models to process. Linkable is default.
	.addResourceFactory(halFactory, "application/hal+json")		// optional. Use this to configure your own Resource factories.
	.register(server);
```

Link Expansion and/or Resource Augmentation
===========================================

HyperExpress supports the ExpansionCallback interface, which allows us to "get in the game" after HyperExpress has created a Resource
instance, copied all the properties and inserted links.  In our ExpansionCallback implementation we can not perform link expansion
(embed related resources from one of the links) or simply add or remove properties from the Resource (maybe depending on role or visibility).

There are two ways to register a callback, both equivalent.  The following register an Expansion callback, MyExpansionCallback()
for a model class, MyModel.class. Now during the request/response life-cycle, the HyperExpressPlugin will invoke the callback anytime
a MyModel instance gets converted to a Resource instance.  This provides the opportunity to augment the Resource object before it gets
serialized.

```java
ExpansionCallback callback = new MyExpansionCallback();

// Option 1 - register with Expander
Expander.registerCallback(MyModel.class, callback);

// Option 2 - register with the HyperExpressPlugin
new HyperExpressPlugin(Linkable.class)
	.registerCallback(MyModel.class, callback);
	.register(server);
```

**Note:** ExpansionCallback.expand(Expansion, Resource) method--the method to implement the callback--does not send in the RestExpress 
request. However, often we want to use data from the request to augment the response.  RestExpress offers the RequestContext object for
this purpose. Request Context is a thread-safe instrument for passing augmentation data from different sources to lower levels in
the framework. For more information, see: https://github.com/RestExpress/RestExpress/blob/master/core/src/java/org/restexpress/util/RequestContext.java
