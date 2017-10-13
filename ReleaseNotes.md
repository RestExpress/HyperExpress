HyperExpress Release Notes
==========================
2.7 SNAPSHOT - in Master
------------------------
* Issue #23 - Bindings in Collections echo
* Issue #18 - Need underlying TokenResolver
* Issue #17 - HyperExpress.isBound()
* Added TokenResolver.contains(String)
* Fixed NPE in ToStringFormatter
* Fixed issue in HyperExpress.createResources() where empty collection returned
* Added no-arg LinkDefinition constructor

2.6 Release - 10 Feb 2016
-------------------------
* Issue #16 - HyperExpressPlugin errors in tests
* Issue #15 - Bind via annotations
* Implemented Strings.join() for Java 1.7
* Changed DefaultTokenResolver to allow optionally binding via annotations via boolean in constructor
* Introduced TokenBindings annotation to enable class-level BindToken annotation
* Fixed issue binding properties up the inheritance hierarchy in Objects.property()
* Added HyperExpress.createResources()
* Added HyperExpress.reset()

2.5 Release - 28 Jul 2015
-------------------------
* Changed Java compiler compatibility back to 1.7 (broken clients)
* Updated for latest RestExpress release (0.11.2)

2.4 Release - 24 Jul 2015
-------------------------
* Changed Java compiler compatibility to 1.8.
* Fixed Issue #11 - Implemented ability to bind multiple values to a query-string parameter and have the parameter repeated.
* Refactored TokenResolver into an interface with DefaultTokenResolver implementation.
* Fixed Issue #14 - Introduced BuilderFactory interface and DefaultBuilderFactory implementation.
* Fixed Issue #12 - Append query strings even if no resolver.

2.3 Release - 13 Mar 2015
-------------------------


2.2 Release - 2 Dec 2014
------------------------


2.1 Release - 5 Sep 2014
------------------------


2.0 Release - 6 Aug 2014
------------------------


1.x Releases - 9 Jan 2013
-------------------------
