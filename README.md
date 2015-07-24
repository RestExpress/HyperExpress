**Build Status** [![Build Status](https://buildhive.cloudbees.com/job/RestExpress/job/HyperExpress/badge/icon)](https://buildhive.cloudbees.com/job/RestExpress/job/HyperExpress/)

**Waffle.io** [![Stories in Ready](https://badge.waffle.io/RestExpress/HyperExpress.png?label=ready)](https://waffle.io/RestExpress/HyperExpress)

[(Video) Introduction to HyperExpress](http://www.youtube.com/watch?v=TQqc7goKVsM&list=UUzaZL1VLtdVTiZ8k07z65jg&feature=share)

HyperExpress
============

Offers a simple way to add hypermedia links to your domain models or DTOs before serializing them to clients. It also
contains classes to help support "link expansion" in your services by using callbacks to enrich the newly created
'Resource' instances before they get serialized.
 
HyperExpress supports several ways to generate links in responses.  If you want to create Link instances
or URLs (using token substitution in URLs patterns) by hand using the Builder pattern (Location header anyone?):

* **HyperExpress** = a Singleton entry-point for HyperExpress functionality.
* **LinkBuilder** = Create Link instance using the builder pattern, optionally using URL token substitution.
* **UrlBuilder** = Create a string URL from a URL pattern, substituting URL tokens to produce a fully-populated URL.

Additionally, HyperExpress now supports the concept of a Resource, which is a generalized interface that
creates a 'presentation model' where your links can be added, other resources can be embedded, etc. with
output rendering being abstracted and dependent on the requested content type.

There are three concepts that play together to accomplish this:

* **RelationshipDefinition** = Use this to describe relationships between resources and namespaces.
* **TokenResolver** = For relationships (in the RelationshipBuilder) that have templated URLs, this class is able to populate the URL parameters, substituting actual values for the tokens (e.g. '{userId}').
* **TokenBinder** = For a collection of resources, resolves tokens in a URL by binding properties from individual objects in the collection to identifiers in the URL.

Additionally, there are a couple of helper classes:

* **MapStringFormat** = which will substitute names in a string with provided values (such as an URL).
* **RelTypes** = contains constants for REST-related standard Iana.org link-relation types.

The HyperExpress-Core project is primarily abstract implementations. Specific functionality is in the sub-projects, such
as HyperExpress-HAL, which has HAL-specific rendering (and parsing) of Resource implementations.

Please see HyperExpress-HAL for usage information: https://github.com/RestExpress/HyperExpress/tree/master/hal

Interested in other functionality?  Drop me a line... let's talk!

How-To
======

There are three phases in which HyperExpress helps you out managing links in your resources;

* Configuring HyperExpress for Media Types
* Defining Static Relationships (between resources)
* Resolving URL tokens (a token in a URL is replaced with an ID)
* Creating Resources using a Factory

After that, it's the 'simple' matter of serializing the Resource to JSON (or XMl, or whatever).
HyperExpress-HAL has a serializer and deserializer for Jackson. Assuming you have your
own Jackson configuration, simply plug these in to your module (see the HyperExpress-HAL README).

Configuring HyperExpress
========================

Maven resources:

For HAL Support
---------------

Stable:

```xml
<dependency>
    <groupId>com.strategicgains</groupId>
    <artifactId>HyperExpress-HAL</artifactId>
    <version>2.3</version>
</dependency>
```

Development snapshot:

```xml
<dependency>
    <groupId>com.strategicgains</groupId>
    <artifactId>HyperExpress-HAL</artifactId>
    <version>2.4-SNAPSHOT</version>
</dependency>
```

For Siren Support
---------------

Stable:

```xml
<dependency>
    <groupId>com.strategicgains</groupId>
    <artifactId>HyperExpress-Siren</artifactId>
    <version>2.3</version>
</dependency>
```

Development snapshot:

```xml
<dependency>
    <groupId>com.strategicgains</groupId>
    <artifactId>HyperExpress-Siren</artifactId>
    <version>2.4-SNAPSHOT</version>
</dependency>
```

Or you can download the jar file directly from http://search.maven.org/#search%7Cga%7C1%7Chyperexpress

Note that if you want to use the SNAPSHOT version, the snapshot repository must be configured in your settings.xml file as follows:

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
Next, add resource factories to HyperExpress so HyperExpress knows which factory to use depending on the MediaType of the request.
In order for createResource() and createCollectionResource() to know what type of resource to create based on content-type,
ResourceFactoryStrategy instances must be registered for each media type support.

Right now, HyperExpress just supports HAL, but there are more media types on the way. To support HAL
(Hypertext Application Language) style resources, register the HAL resource factory as follows:

```java
HyperExpress.registerResourceFactoryStrategy(new HalResourceFactory(), "application/hal+json");
```
If you want HAL links for both application/json and application/hal+json media types, register
them as follows:

```java
HalResourceFactory halFactory = new HalResourceFactory();
HyperExpress.registerResourceFactoryStrategy(halFactory, "application/hal+json");
HyperExpress.registerResourceFactoryStrategy(halFactory, "application/json");
```

HyperExpress-HAL has a serializer and deserializer for Jackson, which you insert into your Jackson module like so:

```java
// Support HalResource (de)serialization.
module.addDeserializer(HalResource.class, new HalResourceDeserializer());
module.addSerializer(HalResource.class, new HalResourceSerializer());
```

Now you're ready to generate Resource representations.

Defining Static Relationships
-----------------------------

HyperExpress has a RelationshipDefinition class, accessed via the HyperExpress.relationships()
method, although it can be created outside the HyperExpress singleton class also.

Let's say we have a Blogging system with Blog, Entry and Comment resources. RelationshipDefinition
syntax for some of the relationships between them might be as follows:

**Caveat:** a Relationship definition captures the canonical or static relationships for a given type (or class). To add dynamic, context-sensitive links, you'll need to add them to the Resource yourself using Resource.addLink() methods.

```java
import static com.strategicgains.hyperexpress.RelTypes.*;
...

HyperExpress.relationships()

// Namespaces, CURIEs
.addNamespaces(
	new Namespace("blog", "http://namespaces.pearson.com/rels/{rel}"),
	new Namespace("foo", "http://namespaces.pearson.com/rels/{rel}"),
	new Namespace("bar", "http://namespaces.pearson.com/rels/{rel}"),
	new Namespace("bat", "http://namespaces.pearson.com/rels/{rel}")
)

// Blog collection (e.g. Read ALL with pagination)
.forCollectionOf(Blog.class)
	.rel(SELF, "/blogs")
		.withQuery("limit={limit}")
		.withQuery("offset={offset}")
	.rel(NEXT, "/blogs?offset={nextOffset}")
		.withQuery("limit={limit}")
		.optional()
	.rel(PREVIOUS, "/blogs?offset={prevOffset}")
		.withQuery("limit={limit}")
		.optional()

// Blog relationships
.forClass(Blog.class)
	.rel("blog:author", "/users/{userId}")
	.rel("blog:entries", "/blogs/{blogId}/entries")
	.rel(SELF, "/blogs/{blogId}")
	.rel(UP, "/blogs")
	.rels("blog:children", "/blogs/{blogId}/entries/{entryId}")

// BlogEntry collection (e.g. Read All with pagination)
.forCollectionOf(BlogEntry.class)
	.rel(SELF, "/blogs/{blogId}/entries")
		.withQuery("limit={limit}")
		.withQuery("offset={offset}")
	.rel(UP, "/blogs/{blogId}")
	.rel(NEXT, "/blogs/{blogId}/entries?offset={nextOffset}")
		.withQuery("limit={limit}")
		.optional()
	.rel(PREVIOUS, "/blogs/{blogId}/entries?offset={prevOffset}")
		.withQuery("limit={limit}")
		.optional()

// BlogEntry relationships
.forClass(BlogEntry.class)
	.rel(SELF, "/blogs/{blogId}/entries/{entryId}")
	.rel("blog:comments", "/blogs/{blogId}/entries/{entryId}/comments")
	.rels("blog:children", "/blogs/{blogId}/entries/{entryId}/comments/{commentId}")
	.rel(UP, "/blogs/{blogId}/entries")

// Comment collection (e.g. Read All with pagination)
.forCollectionOf(Comment.class)
	.rel(SELF, "/blogs/{blogId}/entries/{entryId}/comments")
		.withQuery("limit={limit}")
		.withQuery("offset={offset}")
	.rel("blog:author", "/users/{userId}")
	.rel(UP, "/blogs/{blogId}/entries/{entryId}")
		.title("The parent blog blog entry")
	.rel(NEXT, "/blogs/{blogId}/entries/{entryId}/comments?offset={nextOffset}")
		.withQuery("limit={limit}")
		.optional()
	.rel(PREVIOUS, "/blogs/{blogId}/entries/{entryId}/comments"?offset={prevOffset}")
		.withQuery("limit={limit}")
		.optional()

// Comment relationships
.forClass(Comment.class)
   	.rel(SELF, "/blogs/{blogId}/entries/{entryId}/comments/{commentId}")
   		.title("This very comment")
   	.rel(UP, "/blogs/{blogId}/entries/{entryId}")
   		.title("The parent blog entry")
   	.rel("blog:author", "/users/{userId})
   		.title("The comment author");
```

Note that the namespaces apply to all resources and are oriented toward CURIE format (see: http://www.w3.org/TR/curie/).

Once we have the static relationships defined, it's time to map domain properties to those template URL tokens.

Resolving URL Tokens
--------------------

There are two ways to resolve data properties in a model to template URL tokens. The first
is simply using HyperExpress.bind(String token, String value), which simply maps URL
to the given value.

**HyperExpress.bind(String, String)** - Bind a URL token to a string value. During resource creation, any URL tokens matching the given token string are replace with the provided value. The TokenResolver bindings are specific to the current thread.

The following would bind the token "{blogId}" in any of the above-defined URL templates to the string "1234",
"{entryId}" to "5678" and "{commentId}" to "90123" during creation of a resource:

```java
HyperExpress.bind("blogId", "1234")
	.bind("entryId", "5678")
	.bind("commentId", "90123");
```

The second method is to use a TokenBinder that is effectively a callback and works well for collection resources, where each
item in the collection might have links also.

**HyperExpress.tokenBinder(TokenBinder)** - Uses the TokenBinder as a callback during HyperExpress.createCollectionResource(), binding each object in a collection to the links for that instance.

It binds a TokenBinder to the elements in a collection resource. When a collection resource is created via createCollectionResource(),
the TokenBinder is called for each element in the collection to bind URL tokens to individual properties within the element, if necessary. The TokenBinder is specific to the current thread.

If we were creating a resource from a collection of Comment instances, the following would bind values from each individual
comment to URL tokens.  Specifically, the token "{blogId}" is bound to the blog ID contained in the comment. Respectively, 
"{entryId}" and "{commentId}" are also bound with respect to the comment instance:

```java
// Bind each resources in the collection with link URL tokens, etc. here...
HyperExpress.tokenBinder(new TokenBinder<Comment>()
{
	@Override
	public void bind(Comment comment, TokenResolver resolver)
	{
		resolver.bind("blogId", comment.getBlogId())
			.bind("entryId", comment.getEntryId())
			.bind("commentId", comment.getId());
	}
});
```

Creating Resources
------------------

HyperExpress distinguishes between single resources and collection resources at creation time.
This allows you to create a Resource from a collection of domain instances, or from a single domain object.
In the former, HyperExpress will create a root resource and inject links from the RelationshipDefinition
using the forCollectionOf(Class) group of templates as links.  It will then embed each element of the collection
in that root resource, adding links for each instance using the forClass(Class) group of templates.

**HyperExpress.createResource(Object, String)** creates a resource instance from the object for the given content type.
Properties from the object are copied into the resulting Resource. Also, links are injected for appropriate
relationships defined via HyperExpress.relationships(), using any HyperExpress.bind() or HyperExpress.tokenBinder()
settings to populate the tokens in the URLs.

```java
String responseMediaType = ... // however we determine the outbound media type.
Blog blog = ...  // Let's say it gets read from the database.
Resource resource = HyperExpress.createResource(blog, responseMediaType);
```

The 'resource' object now contains all the non-null properties of 'blog' with links injected. Given the RelationshipDefinition above forClass(Blog.class), 
it has four links with relation types, "blog:author", "blog:entries", "self", "up".  It can now be serialized.

**HyperExpress.createCollectionResource(Collection, Class, String, String)** creates a collection resource, embedding the individual components of the collection.

```java
String responseMediaType = ... // however we determine the outbound media type.
List<Blog> blogs = ...  // say, it gets read from the database.
Resource resource = HyperExpress.createCollectionResource(blogs, Blog.class, "blogs", responseMediaType);
```

In this case, the 'resource' object contains a root resource, with links injected using the forCollectionOf(Blog.class) section above.  The root
resource has at least a "self" link, and possibly "next" and "previous" links. Additionally, each blog instance in the 'blogs' collection
is embedded in the root resource, with each of those embedded resources having their own links, "blog:author", "blog:entries", "self", "up".

Cleaning Up
-----------

HyperExpress maintains a ThreadLocal for all of the token bindings.  This means HyperExpress bindings are thread safe.
However, it also means that HyperExpresss is holding references to all those bindings, which will cause
VM bloat over time.

Once the resources have been created, clean up the token bindings for that thread by calling:

```java
HyperExpress.clearTokenBindings();
```

Link Expansion and/or Resource Augmentation
===========================================

HyperExpress supports the ExpansionCallback interface, which allows us to "get in the game" after HyperExpress has created a Resource
instance, copied all the properties and inserted links.  In our ExpansionCallback implementation we can now perform link expansion
(embed related resources from one of the links) or simply add or remove properties from the Resource (maybe depending on role or visibility).

The following registers an Expansion callback, MyExpansionCallback() for a model class, MyModel.class.

```java
ExpansionCallback callback = new MyExpansionCallback();

// Register with Expander
Expander.registerCallback(MyModel.class, callback);
```

Now after calls to HyperExpress.createResource() or HyperExpress.createCollectionResource(),
We can invoke the callback for the Resource instance.  This provides the opportunity
to augment the Resource object before it gets serialized.

```java
MyClass myClass = new MyClass();
Resource resource = HyperExpress.createResource(MyClass, "application/hal+json");
Expansion expansion = ... // parse the Expansion data from query-string or wherever.

// This will invoke the MyExpansionCallback.expand() method...
resource = Expander.expand(expansion, MyClass.class, resource);
```

BTW, if you're using RestExpress, the HyperExpressPlugin for RestExpress does Resource
creation, Expansion parsing and Expansion callback calling for you. See: https://github.com/RestExpress/HyperExpress/tree/master/restexpress

Implementing ExpansionCallback
------------------------------

Sure, there's no magic to implementing an interface. And the ExpansionCallback interface is
no different.  However, the Expansion object that gets sent as a parameter might be a little
weird. Ideally, it will be parsed from the incoming request, presumably from query-string
parameters. For example, parsed from something like this:

```
http://www.example.com/resource/123?expand=up,related
```

Where 'up' and 'related' are two valid 'rel' types available as links in the resource
payload and we want to support link expansion--including those related resources in the
response instead of just links. For our purposes, let's assume that the 'related' link points
to a collection of related resources and 'up' points to a single resource.

The Expansion instance also contains the desired media type requested via the Accept header.

```java
public class ExampleLinkExpander
implements ExpansionCallback
{
	private ExampleLookupService examples;

	public ExampleLinkExpander(ExampleLookupService exampleService)
    {
		super();
		this.examples = exampleService;
    }

	@Override
	public Resource expand(Expansion expansion, Resource resource)
	{
		// for each of the desired 'rel' expansions selected...
		for(String rel : expansion)
		{
			if ("related".equalsIgnoreCase(rel))
			{
				List<Example> relateds = examples.readRelatedCollection();

				if (relateds.isEmpty())
				{
					resource.addResources(rel, Collections.<Resource> emptyList());
				}
				else
				{
					// Convert each related Example into a Resource and embed it.
					for (Example related : relateds)
					{
						HyperExpress.bind(EXAMPLE_ID, related.getId())
							.bind(EXAMPLE_NAME, urlEncode(crit.name()));

						resource.addResource(rel, HyperExpress.createResource(related, expansion.getMediaType()), true);
					}
				}
			}
			else if ("up".equalsIgnoreCase(rel))
			{
				Example up = examples.readUp();

				if (up == null)
				{
			    	resource.addResource(rel, HyperExpress.createResource(null, mediaType));
				}
				else
				{
					HyperExpress.bind(EXAMPLE_ID, up.getId())
						.bind(EXAMPLE_NAME, urlEncode(up.name()));

					// Convert the up Example into a Resource and embed it.
					resource.addResource(rel, HyperExpress.createResource(up, expansion.getMediaType()));
				}
			}
		}

		return resource;
	}
}
```