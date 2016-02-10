HyperExpress-HAL
================

HyperExpress HAL features. Create and consume HAL (Hypermedia Application Language) resources.

There is actually no serialization built in to HyperExpress-HAL as it assumes you've already
got Jackson baked into your application.  HyperExpress-HAL provides a Jackson serializer
and deserializer for you to insert into your Jackson configuration:

```java
SimpleModule module = ...

module.addSerializer(HalResource.class, new HalResourceSerializer());
module.addDeserializer(HalResource.class, new HalResourceDeserializer());
```

This will auto-magically cause Jackson to serialize a HalResource instance to JSON and
visa versa.

BTW, XML is not yet supported... need it? Give me a holler!

Maven Usage
===========

Stable:

```xml
<dependency>
    <groupId>com.strategicgains</groupId>
    <artifactId>HyperExpress-HAL</artifactId>
    <version>2.6</version>
</dependency>
```

Development:

```xml
<dependency>
    <groupId>com.strategicgains</groupId>
    <artifactId>HyperExpress-HAL</artifactId>
    <version>2.7-SNAPSHOT</version>
</dependency>
```
Or search Maven Central here: http://search.maven.org/#search%7Cga%7C1%7Chyperexpress-hal